import socket
import threading
import os
import time

ENCODER='utf-8'
TLD=("10.33.2.88",8002)

domain=[
    {
        "name":"www.google.com",
        "value":"201.123.21.12",
        "type": "A",
        "ttl": 5
    },
    {
        "name":"www.facebook.com",
        "value":"102.65.28.9",
        "type": "A",
        "ttl": 200
    },
    {
        "name":"www.cs.du.ac.bd",
        "value":"100.100.100.100",
        "type": "A",
        "ttl": 86400
    }
]

def handle_client(data,addr,server_socket):
    print(data)
    flag=0
    for line in domain:
        if line['name']==data :
            if line['type']=='A' or line['type']=='AAAA':
                response='1 '+data+' '+line['value']+' '+str(line['ttl'])
                flag=1
                server_socket.sendto(response.encode(ENCODER),addr)
                break
            else:
                
                break
    if flag==0:
        response='0 '+TLD[0]+' '+str(TLD[1])
        server_socket.sendto(response.encode(ENCODER),addr)
    print(f"Disconnected with {addr[0]}: {addr[1]}")

def checkcache(st):
    for i in domain:
        ed=time.time()
        
        if ed-st>= i["ttl"]:
            print("time is",ed-st)
            domain.remove(i)
    for i in domain:
        print(f"name- {i['name']} value- {i['value']}")

def main():
    HOST='10.33.2.88'
    PORT=8001
    ADDR=(HOST,PORT)
    server_socket=socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_socket.bind(ADDR)
    print("Server is starting...")
    print(f"Server is listening on {HOST} : {PORT}")
    st=time.time()
    try:
        while True:
            response=input("Enter 'data' to see cached data\n")
            if response=='data':
                checkcache(st)
    except KeyboardInterrupt:
        print("Stopped by Ctrl+C")

if __name__== '__main__':
    main()