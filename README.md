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
  - divisors need only be checked up to sqrt(n)
  - if all primes of n-1 are known, n need only be tested for division by those
    primes. 
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
