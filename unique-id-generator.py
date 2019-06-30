#!/usr/bin/env python3

import sys, struct, datetime

# using package pycryptodome, please see web for installation instructions and caveats
from Crypto.Cipher import DES3
from Crypto.Random import get_random_bytes

# constants
digits = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
base = len(digits)
limit = pow(base, 10)

# variables
key = bytearray(24)
des3 = None
generator = 0

def load_key():
    # reads the key file. If not present, creates it
    # this also creates a triple DES cipher with electronic codebook mode that's suitable for
    # encryting and decrypting 64-bit blocks of data independently
    global key, des3
    try:
        with open("key", "rb") as key_file:
            key = key_file.read(24)
    except:
        my_str = "hello world"
        my_str_as_bytes = str.encode(my_str)
        print ('my_str_as_bytes:', my_str_as_bytes )
        type(my_str_as_bytes) # ensure it is byte representation
        my_decoded_str = my_str_as_bytes.decode()
        type(my_decoded_str) # ensure it is string representation
        print ('my_decoded_str:', my_decoded_str )

        key = get_random_bytes(24)
        print ('key:', key )
        print ('key hex:', key.hex() )
        print ('from hex:', bytes.fromhex(key.hex()))
        print (len(bytes.fromhex(key.hex())))
        #~ with open("key", "wb") as key_file:
            #~ key_file.write(key)
    des3 = DES3.new(key, DES3.MODE_ECB)

def load_generator():
    # reads the generator file, if present. Otherwise generator stays at 0
    global generator
    try:
        with open("generator", "r") as generator_file:
            line = generator_file.readline().rstrip('\r\n')
            generator = int(line)
    except:
        pass

def save_generator():
    # this also updates the history file
    global generator
    with open("generator", "w") as generator_file:
        generator_file.write("{}\n".format(generator))
    today = datetime.date.today().isoformat()
    try:
        with open("history", "r+") as history_file:
            pos = line = None
            while True:
                # complicated way of finiding position of last line
                (last_pos, last_line) = (pos, line)
                pos = history_file.tell()
                line = history_file.readline()
                if len(line) == 0:
                    break
            if today == last_line[:10]:
                history_file.seek(last_pos, 0)
            history_file.write("{}:{}\n".format(today, generator))
    except FileNotFoundError:
        with open("history", "w") as history_file:
            history_file.write("{}:{}\n".format(today, generator))

def encrypt(num):
    # encrypt a 64-bit number, result is a 64-bit number
    global des3
    plain = struct.pack("Q", num)
    encrypted = des3.encrypt(plain)
    return struct.unpack("Q", encrypted)[0]

def decrypt(num):
    # decrypt (inversion operation to encrypt)
    global des3
    encrypted = struct.pack("Q", num)
    plain = des3.decrypt(encrypted)
    return struct.unpack("Q", plain)[0]

def encode(num):
    # encode 64-bit number as 10-digit base-64 string
    # num must be positive and less than limit
    # ordering of digits is chosen such that decoding is simple
    global digits, base
    result = ''
    for _ in range(10):
        result = digits[num % base] + result
        num = num // base
    return result

def decode(string):
    # reverse operation, not really robust
    global digits, base
    result = 0
    for c in string:
        result = result * base + digits.find(c)
    return result

def generate(count):
    # generate the given number of strings
    global generator
    result = []
    while len(result) < count:
        generator += 1
        encrypted = encrypt(generator)
        if encrypted < limit:
            result.append(encode(encrypted))
    return result

def usage():
    print("Usage: {} [-g count] [-c code]".format(sys.argv[0]))
    sys.exit(1)

if __name__ == "__main__":
    load_key()
    load_generator()
    if len(sys.argv) < 3 or not sys.argv[1] in ['-g', '-c']:
        usage()
    elif sys.argv[1] == '-g':
        for code in generate(int(sys.argv[2])):
            print(code)
        save_generator()
    elif sys.argv[1] == '-c':
        value = decrypt(decode(sys.argv[2]))
        if value <= generator:
            for line in open("history"):
                (d, g) = line.split(':')
                if int(g) >= value:
                    break
            print("Valid, code was generated on {} (sequence {})".format(d, value))
        else:
            print("Invalid sequence {}".format(value))
            sys.exit(2)