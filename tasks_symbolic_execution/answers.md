# Symbolic & Concolic Execution
## Task 1 - Symbolic Execution with Loops
Consider the function gcd below, which computes the greatest common divisor of two natural numbers using the Euclidean algorithm. The function must be invoked with strictly positive arguments. In this task, you will apply symbolic execution to `gcd`

```c
// requires: a > 0 && b > 0
int gcd(int a, int b) {
	while(b != 0) {
		int tmp = b;
		b = a mod b;
		a = tmp;
	}
	return a;
}
```

**a)** Symbolic execution cannot handle unbounded loops such as in `gcd`. We require static bound and need to explicitly unroll the loop. Apply idea on `gcd` and obtain `bounded_gcd` where loop body from `gcd` is executed **at most** twice.

**Answer**
```c
// requires: a > 0 && b > 0
int bounded_gcd(int a, int b) {
	int c = 0;
	while (b != 0 && c <= 2) {
		int tmp = b;
		b = a mod b;
		a = tmp;
		c = c + 1;
	}
	return a;
}
```

**b)** Perform symbolic execution on `bounded_gcd` to find all symbolic states with a satisfiable path constraint at return statement. Incorporate precondition in your analysis.

**Answer** 
State 0
* $\sigma:$ 
	* $a \rightarrow A_0$
	* $b \rightarrow B_0$
* $\pi:$ 

State 1
* $\sigma$: 
	* $a \rightarrow A_0$ 
	* $b \rightarrow B_0$ 
	* $c \rightarrow 0$
* $\pi:$


State 2 (Continue)
* $\sigma$: 
	* $a \rightarrow A_0$ 
	* $b \rightarrow B_0$ 
	* $c \rightarrow 0$
* $\pi$: $B_0 \neq 0 \land c \leq 2$
* No branching because pre-condition $B_0 > 0$ ensures $B_0 \neq 0$

State 3
* $\sigma$: 
	* $a \rightarrow A_0$ 
	* $b \rightarrow B_0$ 
	* $c \rightarrow 0$ 
	* $t\rightarrow B_0$
* $\pi$: 

State 4
* $\sigma$: 
	* $a \rightarrow A_0$ 
	* $b \rightarrow (A_0 \mod B_0)$ 
	* $c \rightarrow 0$ 
	* $t\rightarrow B_0$
* $\pi$:

State 5
* $\sigma$: 
	* $a \rightarrow B_0$ 
	* $b \rightarrow (A_0 \mod B_0)$ 
	* $c \rightarrow 0$ 
	* $t\rightarrow B_0$
* $\pi$:

State 6
* $\sigma$: 
	* $a \rightarrow B_0$ 
	* $b \rightarrow (A_0 \mod B_0)$ 
	* $c \rightarrow 1$ 
	* $t\rightarrow B_0$
* $\pi$:

State 7 (Return)
* $\sigma$: 
	* $a \rightarrow B_0$ 
	* $b \rightarrow (A_0 \mod B_0)$ 
	* $c \rightarrow 1$ 
* $\pi$: $(A_0 \mod B_0) = 0 \land c \leq 2$
* Pre-condition $(A_0 > 0, B_0 > 0)$ does not prevent $(A_0 \mod B_0) = 0$, therefore we have two branches!


State 8 (Continue)
* $\sigma$: 
	* $a \rightarrow B_0$ 
	* $b \rightarrow (A_0 \mod B_0)$ 
	* $c \rightarrow 1$ 
* $\pi$: $(A_0 \mod B_0) \neq 0 \land c \leq 2$


State 9
* $\sigma$: 
	* $a \rightarrow B_0$ 
	* $b \rightarrow (A_0 \mod B_0)$ 
	* $c \rightarrow 1$ 
	* $t\rightarrow (A_0 \mod B_0)$
* $\pi$: 

State 10
* $\sigma$: 
	* $a \rightarrow B_0$ 
	* $b \rightarrow (B_0 \mod (A_0 \mod B_0))$ 
	* $c \rightarrow 1$ 
	* $t\rightarrow (A_0 \mod B_0)$
* $\pi$: 

State 11
* $\sigma$: 
	* $a \rightarrow (A_0 \mod B_0)$ 
	* $b \rightarrow (B_0 \mod (A_0 \mod B_0))$ 
	* $c \rightarrow 1$ 
	* $t\rightarrow (A_0 \mod B_0)$
* $\pi$: 

State 12
* $\sigma$: 
	* $a \rightarrow (A_0 \mod B_0)$ 
	* $b \rightarrow (B_0 \mod (A_0 \mod B_0))$ 
	* $c \rightarrow 2$ 
	* $t\rightarrow (A_0 \mod B_0)$
* $\pi$: 

State 13 (End)
* $\sigma$: 
	* $a \rightarrow (A_0 \mod B_0)$ 
	* $b \rightarrow (B_0 \mod (A_0 \mod B_0))$ 
	* $c \rightarrow 2$ 
* $\pi$: $c \nleq 2$
* No need to branch
* Condition $B_0 \mod (A_0 \mod B_0)) \neq 0$ should not be considered as path condition is solely determined by $c$ at this point. 

**c)** Transformation performed in (a) changed behaviour of `gcd` for some inputs. Provide positive values for $a$ and $b$ such that `gcd(a, b)` and `bounded_gcd(a, b)` return different results.
What does this mean for the output of the symbolic execution? Is it still an under-approximation of the result computed by `gcd`?

**Answer**
After symbolic execution, we only need to look at $B_0 \mod (A_0 \mod B_0)) \neq 0$ 

In `bounded_gcd(a, b)`, this condition is irrelevant due to the bound but in real `gcd`, the loop may continue if this condition holds true. 

Therefore, if $A_0=2$ and $B_0=3$, `bounded_gcd` will return $2$ but `gcd` will return $1$


Because `bounded_gcd` returns a result that `gcd` would never return for the same set of inputs, it is not a valid under-approximation. A valid under-approximation would ensure that they behave identical for the same set of inputs. In other words, it needs to be a subset of all execution paths of `gcd` to be an under-approximation. So all bugs found in `bounded_gcd` can also occur in `gcd` but not vice versa. (Otherwise you cannot use `bounded_gcd` as a proxy for `gcd`!)

**d)** How can you modify transformation from (a) and the semantics of symbolic execution such that it computes an under-approximation of `gcd`?
*Hint*: Try to rule out executions not possible in `gcd` using path constraints.

**Answer**