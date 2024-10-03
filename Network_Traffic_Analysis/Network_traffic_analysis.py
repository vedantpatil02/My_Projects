import argparse
from telnetlib import IP
from scapy.all import *
import matplotlib.pyplot as plt

# Dictionary to store the count of each protocol
protocol_count = {}

def packet_callback(packet):
    # Check if the packet is an IP packet
    if IP in packet:
        # Extract the protocol from the IP packet
        protocol = packet[IP].proto
        
        # Increment the count for the protocol
        if protocol in protocol_count:
            protocol_count[protocol] += 1
        else:
            protocol_count[protocol] = 1

def analyze_traffic(interface, count):
    # Start sniffing packets
    sniff(iface=interface, prn=packet_callback, count=count)

def plot_results():
    # Create a list of protocol names
    protocol_names = [
        'ICMP', 'TCP', 'UDP', 'Other'
    ]

    # Create a list of protocol counts
    protocol_counts = [
        protocol_count.get(1, 0),
        protocol_count.get(6, 0),
        protocol_count.get(17, 0),
        sum(v for k, v in protocol_count.items() if k not in [1, 6, 17])
    ]

    # Create a bar graph of the protocol counts
    plt.bar(protocol_names, protocol_counts)
    plt.xlabel('Protocol')
    plt.ylabel('Count')
    plt.title('Network Traffic Analysis')
    plt.show()

def main():
    # Create the argument parser
    parser = argparse.ArgumentParser(description='Network Traffic Analysis Tool')
    parser.add_argument('-i', '--interface', help='Network interface to capture packets', required=True)
    parser.add_argument('-c', '--count', type=int, help='Number of packets to capture', required=True)
    args = parser.parse_args()

    # Analyze the network traffic
    analyze_traffic(args.interface, args.count)

    # Plot the results
    plot_results()

if __name__ == '__main__':
    main()