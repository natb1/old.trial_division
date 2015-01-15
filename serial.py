from math import sqrt

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

if __name__ == '__main__':
    import sys
    n = int(sys.argv[1])
    print(all_primes(n))
