#!/usr/bin/env python3
"""
md_to_pdf.py — Convert Markdown files to PDF, with full support for:
  - Fenced code blocks (with syntax highlighting)
  - Inline images (local files and remote URLs)
  - Tables, blockquotes, headings, lists
  - Batch conversion of multiple files

Dependencies (install once):
    pip install markdown weasyprint pygments

Usage:
    python md_to_pdf.py README.md
    python md_to_pdf.py doc.md -o report.pdf
    python md_to_pdf.py *.md --output-dir ./pdfs
"""

import argparse
import sys
import base64
import re
from pathlib import Path


# ── Stylesheet embedded in the script so no extra files are needed ───────────

CSS = """
/* ── Page layout ── */
@page {
    margin: 2.5cm 2.8cm;
    @bottom-center {
        content: counter(page) " / " counter(pages);
        font-size: 9pt;
        color: #888;
    }
}

/* ── Base typography ── */
body {
    font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
    font-size: 11pt;
    line-height: 1.7;
    color: #1a1a1a;
}

h1, h2, h3, h4, h5, h6 {
    font-weight: 600;
    margin-top: 1.4em;
    margin-bottom: 0.4em;
    line-height: 1.25;
    color: #111;
}
h1 { font-size: 22pt; border-bottom: 2px solid #e0e0e0; padding-bottom: 6px; }
h2 { font-size: 17pt; border-bottom: 1px solid #e8e8e8; padding-bottom: 4px; }
h3 { font-size: 14pt; }
h4 { font-size: 12pt; }

p  { margin: 0.6em 0; }
a  { color: #0066cc; text-decoration: none; }

/* ── Code ── */
code {
    font-family: "Cascadia Code", "Fira Code", "Consolas", "Courier New", monospace;
    font-size: 9.5pt;
    background: #f5f5f5;
    padding: 1px 5px;
    border-radius: 3px;
    color: #c7254e;
}

pre {
    background: #f8f8f8;
    border: 1px solid #ddd;
    border-left: 4px solid #4a9eff;
    border-radius: 5px;
    padding: 14px 16px;
    overflow-x: auto;
    page-break-inside: avoid;
    margin: 1em 0;
}

/* Pygments syntax-highlighted blocks */
pre code, div.highlight pre {
    background: transparent;
    padding: 0;
    border: none;
    border-radius: 0;
    color: inherit;
    font-size: 9pt;
}

/* ── Pygments (monokai-style) token colours ── */
.highlight .hll { background-color: #ffffcc }
.highlight .c  { color: #888; font-style: italic }   /* Comment */
.highlight .k  { color: #0077aa; font-weight: bold } /* Keyword */
.highlight .n  { color: #1a1a1a }                    /* Name */
.highlight .o  { color: #555 }                       /* Operator */
.highlight .s  { color: #d44950 }                    /* String */
.highlight .m  { color: #007788 }                    /* Number */
.highlight .kn { color: #0077aa; font-weight: bold } /* Keyword.Namespace */
.highlight .kd { color: #0077aa; font-weight: bold } /* Keyword.Declaration */
.highlight .nb { color: #335 }                       /* Name.Builtin */
.highlight .nf { color: #006699 }                    /* Name.Function */
.highlight .nc { color: #0077aa }                    /* Name.Class */
.highlight .cm { color: #888; font-style: italic }
.highlight .cp { color: #558; font-weight: bold }
.highlight .c1 { color: #888; font-style: italic }
.highlight .cs { color: #888; font-weight: bold; font-style: italic }

/* ── Images ── */
img {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 1em auto;
    border-radius: 4px;
}

/* ── Tables ── */
table {
    width: 100%;
    border-collapse: collapse;
    margin: 1em 0;
    font-size: 10.5pt;
    page-break-inside: avoid;
}
th, td {
    border: 1px solid #d0d0d0;
    padding: 7px 12px;
    text-align: left;
}
th {
    background: #f0f4f8;
    font-weight: 600;
}
tr:nth-child(even) td { background: #fafafa; }

/* ── Blockquote ── */
blockquote {
    margin: 1em 0;
    padding: 8px 18px;
    border-left: 4px solid #ccc;
    background: #fafafa;
    color: #555;
    font-style: italic;
}

/* ── Lists ── */
ul, ol { margin: 0.5em 0; padding-left: 1.8em; }
li     { margin: 0.25em 0; }

/* ── Horizontal rule ── */
hr {
    border: none;
    border-top: 1px solid #ddd;
    margin: 1.5em 0;
}
"""


# ── HTML wrapper ─────────────────────────────────────────────────────────────

HTML_TEMPLATE = """\
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>{title}</title>
<style>
{css}
</style>
</head>
<body>
{body}
</body>
</html>
"""


# ── Local-image → data-URI embedding ─────────────────────────────────────────

def embed_local_images(html: str, md_path: Path) -> str:
    """Replace src="…" for local image files with base64 data URIs."""
    MIME = {
        ".png": "image/png", ".jpg": "image/jpeg", ".jpeg": "image/jpeg",
        ".gif": "image/gif", ".svg": "image/svg+xml", ".webp": "image/webp",
        ".bmp": "image/bmp",
    }

    def replacer(m):
        src = m.group(1)
        # Skip remote URLs
        if src.startswith(("http://", "https://", "data:")):
            return m.group(0)
        img_path = (md_path.parent / src).resolve()
        if not img_path.exists():
            print(f"  ⚠  Image not found, skipping embed: {img_path}", file=sys.stderr)
            return m.group(0)
        suffix = img_path.suffix.lower()
        mime = MIME.get(suffix, "image/png")
        b64 = base64.b64encode(img_path.read_bytes()).decode()
        return f'src="data:{mime};base64,{b64}"'

    return re.sub(r'src="([^"]*)"', replacer, html)


# ── Conversion logic ──────────────────────────────────────────────────────────

def convert(md_path: Path, pdf_path: Path) -> None:
    try:
        import markdown
        from markdown.extensions.codehilite import CodeHiliteExtension
        from markdown.extensions.fenced_code import FencedCodeExtension
    except ImportError:
        sys.exit("❌  Missing dependency: pip install markdown")

    try:
        from weasyprint import HTML as WeasyprintHTML, CSS as WeasyprintCSS
    except ImportError:
        sys.exit("❌  Missing dependency: pip install weasyprint")

    print(f"  Converting  {md_path}  →  {pdf_path}")

    md_text = md_path.read_text(encoding="utf-8")

    # Convert Markdown → HTML (with fenced code + syntax highlighting)
    extensions = [
        FencedCodeExtension(),
        CodeHiliteExtension(linenums=False, css_class="highlight"),
        "tables",
        "toc",
        "attr_list",
        "def_list",
        "footnotes",
        "meta",
        "nl2br",
    ]
    body_html = markdown.markdown(md_text, extensions=extensions)

    # Embed local images as data URIs so WeasyPrint finds them
    body_html = embed_local_images(body_html, md_path)

    html = HTML_TEMPLATE.format(
        title=md_path.stem,
        css=CSS,
        body=body_html,
    )

    # Render to PDF
    pdf_path.parent.mkdir(parents=True, exist_ok=True)
    WeasyprintHTML(string=html, base_url=str(md_path.parent)).write_pdf(str(pdf_path))
    print(f"  ✅  Saved  {pdf_path}  ({pdf_path.stat().st_size // 1024} KB)")


# ── CLI ───────────────────────────────────────────────────────────────────────

def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(
        description="Convert Markdown files to PDF (supports code blocks & images).",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__,
    )
    p.add_argument("inputs", nargs="+", metavar="FILE.md",
                   help="One or more Markdown files (glob patterns accepted via shell).")
    p.add_argument("-o", "--output", metavar="FILE.pdf",
                   help="Output PDF path. Only valid when converting a single file.")
    p.add_argument("--output-dir", metavar="DIR",
                   help="Directory for output PDFs when converting multiple files. "
                        "Defaults to the same directory as each source file.")
    return p


def main():
    args = build_parser().parse_args()
    inputs = [Path(p) for p in args.inputs]

    # Validate
    for f in inputs:
        if not f.exists():
            sys.exit(f"❌  File not found: {f}")
        if f.suffix.lower() not in (".md", ".markdown", ".mdown", ".mkd"):
            print(f"  ⚠  {f} doesn't look like a Markdown file — converting anyway.",
                  file=sys.stderr)

    if args.output and len(inputs) > 1:
        sys.exit("❌  --output can only be used with a single input file.")

    output_dir = Path(args.output_dir) if args.output_dir else None

    for md_path in inputs:
        if args.output:
            pdf_path = Path(args.output)
        elif output_dir:
            pdf_path = output_dir / (md_path.stem + ".pdf")
        else:
            pdf_path = md_path.with_suffix(".pdf")
        convert(md_path, pdf_path)

    print(f"\nDone — converted {len(inputs)} file(s).")


if __name__ == "__main__":
    main()
