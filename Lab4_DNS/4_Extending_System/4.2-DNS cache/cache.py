import socket
import struct
import time

IP = ''
PORT = 4487
ADDR = (IP, PORT)
SIZE = 1024
FORMAT = "utf-8"
cached = {}


def encode_msg(message):
    data = message.split()
    name = data[0]
    type = data[1]

    flag = 0
    q = 0
    a = 1
    auth_rr = 0
    add_rr = 0

    ms = (name + ' ' + type).encode('utf-8')
    packed_data = struct.pack(f"6H{len(ms)}s", 50, flag, q, a, auth_rr, add_rr, ms)
    return packed_data


def decode_msg(msg):
    header = struct.unpack("6H", msg[:12])
    ms = msg[12:].decode('utf-8')

    print('\n Before Decoding')
    print(msg)

    print('\n After Decoding')
    print({header}, {ms})

    return ms


def main():
    client = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    while True:
        message = input("Enter an address or enter 'data' to see the cached data: ")
        if message == "data":
            print('\n ______Cached Data______\n')
            for i in cached:
                print(i + ' ' + cached[i][0])
            print('\n ______End______\n')
        else:
            try:
                client.sendto(message.encode(FORMAT), ADDR)
                msg, addr = client.recvfrom(SIZE)
                msg = decode_msg(msg)
                print('Received response:', msg)
                qu = msg.split()
                if qu[0] == 'error':
                    print('Error:', qu[1])
                else:
                    cached[qu[0]] = (qu[1], qu[2], qu[3])
            except Exception as e:
                print("An error occurred:", e)


if __name__ == "__main__":
    main()
