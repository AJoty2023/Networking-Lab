import socket

HOST='10.33.2.88'
PORT=8001
ADDR=(HOST,PORT)
ENCODER='utf-8'

def main():
    client_socket=socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    message= input("Enter domain name:\n")
    client_socket.sendto(message.encode(ENCODER),ADDR)

    response,addr= client_socket.recvfrom(1024)
    response=response.decode(ENCODER)
    if response=="$$$":
        print("No data found...")
    else:
        print("Response from server....")
        print(response)

if __name__== '__main__':
    main()
