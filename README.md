solution that satisfies the requirement:

Print all prime numbers exactly once in any order from 1..N.

given the following constraints:
- N must be provided at the command line
- Must use Java or Scala
- Must use a "Dealer" and two or more "Workers"
- "algorithm which inspects each integer separately"

## method
- choice of algorithm: last constraint is interpreted to mean the algorithm
must be some variation of trial division
- optimizations of serial trial division:
  - if all primes of n-1 are known, n need only be tested for division by those
    primes. 
  - if one divisor of a number is known, it does not need to continue checking
    for additional divisors.
  - divisors need only be checked up to sqrt(n)
  - asumes N is positive
```
def all_primes(n):
    primes = []
    for i in range(2, n+1): 
        if not _divisor_found(i, primes):
            primes.append(i)
    return primes

def _divisor_found(i, primes):
    sqrti = sqrt(i)
    for prime in primes:
        if prime > sqrti:
            break
        elif i % prime == 0:
            return True
    return False
```

## parallelization strategies: (TODO: probably cut this back)
- decomposition of `all_primes`: `all_primes` can be decomposed into
  `_divisor_found` subproblems. The result of `all_primes` is the
  concatenation of all `_divisor_found` subproblems.
  However, the first optimization to the serial algorithm uses shared
  state (`primes`) and therefore will not work as is. If parralelization <
  partitions then some synchronization
  will be required anyway and so the optimization can be used in part. The 
  `_divisor_found` algorithm will just have to be aware that its view of `primes`
  might be stale.
- decomposition of `_divisor_found`: `divisor_found` can be decomposed into
  trial division subproblems. The result of `_divisor_found` is the "and" of
  all trial division subproblems. However, the second optimization of the
  serial algorithm uses a shared conditional. If parralelization < partitions
  then some synchronization will be required anyway and so the optimization
  can be used in part.
- choice of parameters:
  - partitions of `all_primes` and `_divisor_found`: The choice of the number of subproblems is a
    tradeoff between concurrency and overhead. 
  - parallelization: `all_primes` and `_divisor_found` will share workers. If
    either were blocking, or if either had priority over the other I would
    assign each its own queue. The number of workers is a tradeoff between
    parallelism and overhead. 
- optimizations:
    - The time to complete
    `_divisor_found` subproblems is a function of `i`. If using the "early bailout"
    optimization the time to complete trial division subproblems is also a
    function of `i` (primes aren't evenly distributed). A potential optimization 
    would be to makes partitions of different sizes. 
    - additonal optimizations might be required if it were specified that the
    system had to process concurrent N's.
