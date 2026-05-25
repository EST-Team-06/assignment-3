#!/usr/bin/env python3
"""
md_to_pdf.py — Convert Markdown files to PDF, with full support for:
  - Fenced code blocks (with syntax highlighting)
  - Inline images (local files and remote URLs)
  - LaTeX math: inline  $…$  and display  $$…$$  blocks
  - Tables, blockquotes, headings, lists
  - Batch conversion of multiple files

Dependencies (install once):
    pip install markdown pymdown-extensions weasyprint pygments matplotlib

Usage:
    python md_to_pdf.py README.md
    python md_to_pdf.py doc.md -o report.pdf
    python md_to_pdf.py *.md --output-dir ./pdfs

LaTeX syntax supported:
    Inline  : The formula $E = mc^2$ appears mid-sentence.
    Display : $$\\int_0^\\infty e^{-x^2} dx = \\frac{\\sqrt{\\pi}}{2}$$
"""

import argparse
import base64
import io
import re
import sys
from pathlib import Path


# ── Stylesheet ────────────────────────────────────────────────────────────────

CSS = """
@page {
    margin: 2.5cm 2.8cm;
    @bottom-center {
        content: counter(page) " / " counter(pages);
        font-size: 9pt;
        color: #888;
    }
}

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
pre code, div.highlight pre {
    background: transparent;
    padding: 0;
    border: none;
    border-radius: 0;
    color: inherit;
    font-size: 9pt;
}

/* ── Pygments tokens ── */
.highlight .hll { background-color: #ffffcc }
.highlight .c  { color: #888; font-style: italic }
.highlight .k  { color: #0077aa; font-weight: bold }
.highlight .n  { color: #1a1a1a }
.highlight .o  { color: #555 }
.highlight .s  { color: #d44950 }
.highlight .m  { color: #007788 }
.highlight .kn { color: #0077aa; font-weight: bold }
.highlight .kd { color: #0077aa; font-weight: bold }
.highlight .nb { color: #335 }
.highlight .nf { color: #006699 }
.highlight .nc { color: #0077aa }
.highlight .cm { color: #888; font-style: italic }
.highlight .cp { color: #558; font-weight: bold }
.highlight .c1 { color: #888; font-style: italic }
.highlight .cs { color: #888; font-weight: bold; font-style: italic }

/* ── Math (SVG images rendered from LaTeX) ── */
img.math-inline {
    display: inline;
    vertical-align: middle;
    margin: 0 2px;
    height: 1.15em;        /* scale with surrounding text */
}
img.math-display {
    display: block;
    margin: 1em auto;
    max-width: 100%;
}

/* ── Regular images ── */
img:not(.math-inline):not(.math-display) {
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
th, td { border: 1px solid #d0d0d0; padding: 7px 12px; text-align: left; }
th { background: #f0f4f8; font-weight: 600; }
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

ul, ol { margin: 0.5em 0; padding-left: 1.8em; }
li     { margin: 0.25em 0; }

hr { border: none; border-top: 1px solid #ddd; margin: 1.5em 0; }
"""

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


# ── LaTeX → SVG (via matplotlib mathtext) ────────────────────────────────────

def _latex_to_svg_data_uri(latex: str, display: bool) -> str:
    """
    Render a LaTeX expression to an SVG data-URI using matplotlib's mathtext
    engine.  No internet connection required.

    Supports the full set of TeX math commands that matplotlib understands:
    fractions, integrals, Greek letters, summations, square roots, etc.
    """
    import matplotlib
    matplotlib.use("Agg")           # headless — no display needed
    import matplotlib.pyplot as plt
    import matplotlib.mathtext as mathtext

    # Wrap in $…$ if not already wrapped (mathtext requires it)
    expr = latex.strip()
    if not (expr.startswith("$") and expr.endswith("$")):
        expr = f"${expr}$"

    fontsize = 14 if display else 12

    # Parse into a PNG first, then convert to SVG-friendly PNG data-URI.
    # (matplotlib's SVG backend for mathtext has layout bugs; PNG is reliable.)
    fig = plt.figure(figsize=(0.01, 0.01))
    fig.patch.set_alpha(0)
    try:
        txt = fig.text(0, 0, expr, fontsize=fontsize, color="#1a1a1a")
        # Auto-size figure around the rendered text
        buf = io.BytesIO()
        fig.savefig(buf, format="png", dpi=150,
                    bbox_inches="tight", pad_inches=0.04,
                    transparent=True)
        buf.seek(0)
        b64 = base64.b64encode(buf.read()).decode()
        return f"data:image/png;base64,{b64}"
    except Exception as exc:
        print(
            f"  ⚠  Math render failed for '{latex[:40]}': {exc}", file=sys.stderr)
        return ""
    finally:
        plt.close(fig)


# Cache so identical expressions aren't rendered twice
_math_cache: dict[str, str] = {}


def latex_to_img_tag(latex: str, display: bool) -> str:
    key = (latex, display)
    if key not in _math_cache:
        _math_cache[key] = _latex_to_svg_data_uri(latex, display)
    uri = _math_cache[key]
    if not uri:
        # Fallback: show the raw LaTeX in a <code> span
        escaped = latex.replace("&", "&amp;").replace("<", "&lt;")
        return f'<code class="math-fallback">{escaped}</code>'
    css_class = "math-display" if display else "math-inline"
    return f'<img class="{css_class}" src="{uri}" alt="{latex}">'


# ── Math pre-processor  ───────────────────────────────────────────────────────
# Runs BEFORE the Markdown parser so that $…$ / $$…$$ are never mangled
# by Markdown's own escape/emphasis rules.

# Placeholder scheme: we stash rendered <img> tags keyed by a unique token,
# then put them back after Markdown has done its work.

_PLACEHOLDER_PREFIX = "\x02MATH"
_PLACEHOLDER_SUFFIX = "\x03"


def preprocess_math(md_text: str) -> tuple[str, dict[str, str]]:
    """
    Find all $$…$$ (display) and $…$ (inline) math spans, render each to an
    <img> tag, and replace them in the source with opaque placeholders that
    Markdown won't touch.

    Returns (modified_text, placeholder_map).
    """
    placeholders: dict[str, str] = {}
    counter = [0]

    def make_placeholder(img_tag: str) -> str:
        token = f"{_PLACEHOLDER_PREFIX}{counter[0]}{_PLACEHOLDER_SUFFIX}"
        placeholders[token] = img_tag
        counter[0] += 1
        return token

    # ── display math  $$…$$ ──────────────────────────────────────────────────
    # Allow multi-line display blocks.
    def replace_display(m):
        body = m.group(1).strip()
        return make_placeholder(latex_to_img_tag(body, display=True))

    md_text = re.sub(
        r"\$\$(.*?)\$\$",
        replace_display,
        md_text,
        flags=re.DOTALL,
    )

    # ── inline math  $…$  ────────────────────────────────────────────────────
    # Dollar signs that are:
    #   • not escaped with \
    #   • not immediately followed by a digit (currency: $5, $10)
    #   • content does not span a blank line
    def replace_inline(m):
        body = m.group(1).strip()
        return make_placeholder(latex_to_img_tag(body, display=False))

    md_text = re.sub(
        r"(?<!\\)\$(?!\d)(.+?)(?<!\\)\$",
        replace_inline,
        md_text,
        flags=re.DOTALL,
    )

    return md_text, placeholders


def restore_placeholders(html: str, placeholders: dict[str, str]) -> str:
    for token, img_tag in placeholders.items():
        # The Markdown parser may have wrapped the placeholder in a <p>; that's
        # fine — the img tags will sit inside <p> or inline as appropriate.
        html = html.replace(token, img_tag)
    return html


# ── Local-image embedding ─────────────────────────────────────────────────────

def embed_local_images(html: str, md_path: Path) -> str:
    MIME = {
        ".png": "image/png", ".jpg": "image/jpeg", ".jpeg": "image/jpeg",
        ".gif": "image/gif", ".svg": "image/svg+xml", ".webp": "image/webp",
        ".bmp": "image/bmp",
    }

    def replacer(m):
        src = m.group(1)
        if src.startswith(("http://", "https://", "data:")):
            return m.group(0)
        img_path = (md_path.parent / src).resolve()
        if not img_path.exists():
            print(
                f"  ⚠  Image not found, skipping embed: {img_path}", file=sys.stderr)
            return m.group(0)
        mime = MIME.get(img_path.suffix.lower(), "image/png")
        b64 = base64.b64encode(img_path.read_bytes()).decode()
        return f'src="data:{mime};base64,{b64}"'

    return re.sub(r'src="([^"]*)"', replacer, html)


# ── Core conversion ───────────────────────────────────────────────────────────

def convert(md_path: Path, pdf_path: Path) -> None:
    try:
        import markdown
        from markdown.extensions.codehilite import CodeHiliteExtension
        from markdown.extensions.fenced_code import FencedCodeExtension
    except ImportError:
        sys.exit("❌  Missing dependency: pip install markdown")

    try:
        from weasyprint import HTML as WeasyprintHTML
    except ImportError:
        sys.exit("❌  Missing dependency: pip install weasyprint")

    print(f"  Converting  {md_path}  →  {pdf_path}")

    md_text = md_path.read_text(encoding="utf-8")

    # 1. Extract & render math before Markdown sees it
    md_text, placeholders = preprocess_math(md_text)

    # 2. Markdown → HTML
    extensions = [
        FencedCodeExtension(),
        CodeHiliteExtension(linenums=False, css_class="highlight"),
        "tables", "toc", "attr_list", "def_list", "footnotes", "meta", "nl2br",
    ]
    body_html = markdown.markdown(md_text, extensions=extensions)

    # 3. Put math <img> tags back in place
    body_html = restore_placeholders(body_html, placeholders)

    # 4. Embed local images
    body_html = embed_local_images(body_html, md_path)

    html = HTML_TEMPLATE.format(title=md_path.stem, css=CSS, body=body_html)

    # 5. Render to PDF
    pdf_path.parent.mkdir(parents=True, exist_ok=True)
    WeasyprintHTML(string=html, base_url=str(
        md_path.parent)).write_pdf(str(pdf_path))
    kb = pdf_path.stat().st_size // 1024
    print(f"  ✅  Saved  {pdf_path}  ({kb} KB)")


# ── CLI ───────────────────────────────────────────────────────────────────────

def build_parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(
        description="Convert Markdown → PDF (code blocks, images, LaTeX math).",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__,
    )
    p.add_argument("inputs", nargs="+", metavar="FILE.md")
    p.add_argument("-o", "--output", metavar="FILE.pdf",
                   help="Output PDF path (single-file mode only).")
    p.add_argument("--output-dir", metavar="DIR",
                   help="Output directory for batch conversions.")
    return p


def main():
    args = build_parser().parse_args()
    inputs = [Path(p) for p in args.inputs]

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
