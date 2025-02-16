from scapy.all import sniff, IP
import sys

def packet_callback(packet):
    if IP in packet:
        print(f"Source IP: {packet[IP].src} -> Destination IP: {packet[IP].dst}")

def main():
    try:
        # First, let's check if we're running with admin/root privileges
        if sys.platform.startswith('win32'):
            try:
                import ctypes
                is_admin = ctypes.windll.shell32.IsUserAnAdmin()
                if not is_admin:
                    print("Not running with administrator privileges. Please run as administrator.")
                    return
            except:
                print("Could not determine administrator status.")
        else:  # Unix-like systems
            if os.geteuid() != 0:
                print("Not running with root privileges. Please run with sudo.")
                return

        print("Starting packet capture... Press Ctrl+C to stop")
        # Capture only 10 packets for testing
        sniff(prn=packet_callback, count=10)
        
    except PermissionError:
        print("\nERROR: Permission denied. Please run with sudo/administrator privileges.")
    except Exception as e:
        print(f"\nERROR: {str(e)}")
        print("\nTroubleshooting steps:")
        print("1. Make sure you have scapy installed: pip install scapy")
        print("2. On Windows, install Npcap from: https://npcap.com/")
        print("3. Run the script with administrator privileges")
        if sys.platform.startswith('win32'):
            print("4. Check if your antivirus is blocking packet capture")

if __name__ == '__main__':
    import os
    main()
