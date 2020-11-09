#!/bin/bash
qemu-system-i386 -m 256 -boot d -cdrom ./kolibri.iso -usb -usbdevice tablet -machine type=q35,accel=kvm 
