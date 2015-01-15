solution that satisfies the requirement:

Print all prime numbers exactly once in any order from 1..N.

given the following constraints:
- N must be provided at the command line
- Must use Java or Scala
- Must use a "Dealer" and two or more "Workers"
- "algorithm which inspects each integer separately"

## method
- choice of technology: I am using Akka with an Actor model because this is a
  new technology to me and it seems like the best documented usage.
- choice of algorithm: the last constraint is interpreted to mean the algorithm
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
        if _is_prime(i, primes):
            primes.append(i)
    return primes

def _is_prime(i, primes):
    sqrti = sqrt(i)
    for prime in primes:
        if prime > sqrti:
            break
        elif i % prime == 0:
            return False
    return True
```
- parallelization strategy:
  - decomposition of `all_primes`: `all_primes` can be decomposed into
    `_is_prime` subproblems. The result of `all_primes` is the
    concatenation of all `_is_prime` subproblems.
  - choice of parameters:
    - partitions of `all_primes`: The choice of the number of subproblems is a
      tradeoff between concurrency and messaging overhead.
    - parallelization: The number of workers is a tradeoff between
      parallelism and resources. 
- optimizations:
  - The first optimization to the serial algorithm uses shared state (`primes`)
    and therefore will not work as is. If workers synchronized their results
    with the Dealer (or an accumulator) then the optimization could still be
    used in part. There would just be a tradeoff between the "consistency" and
    "availability" of `primes`.
  - The time to complete
    `_is_prime` subproblems is a function of `i`. Partition sizes could be
    made a function of `i` to distribute the load.
  - `_is_prime` can be decomposed into
    trial division subproblems. The result of `_is_prime` is the "and" of
    all trial division subproblems. However, the second optimization of the
    serial algorithm uses shared state. Benchmarking may show this is
    preferable to partitioning `all_primes`, or they could both be partitioned
    if more concurrency was needed.
