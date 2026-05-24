#include <stdbool.h>
#include <assert.h>
#include <klee/klee.h>

int my_pow(int b, int e) {
    int r = b;
    for (int i = 1; i < e; i++) {
        r = r * b;
    }
    return r;
}

int pow_client(int b, int e) {
    int r = my_pow(b, e);

    if (e % 2 == 0) {
        if (r < 0) {
            klee_assert(false);
        }
    }

    return r;
}

int main() {
    int b;
    int e;

    klee_make_symbolic(&b, sizeof(b), "b");
    klee_make_symbolic(&e, sizeof(e), "e");

    klee_assume(e >= 0);

    pow_client(b, e);

    return 0;
}