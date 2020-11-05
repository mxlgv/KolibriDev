#!/bin/bash
IMG_DIR=build_img
ISO_DIR=build_iso

mkdir tmp
dd if=/dev/zero of=$ISO_DIR/kolibri.img bs=1k count=1440
mkfs.vfat -F12 $ISO_DIR/kolibri.img 
dd if=boot_fat12.bin of=$ISO_DIR/kolibri.img conv=notrunc bs=512 count=1
sudo mount -o loop,rw,sync $ISO_DIR/kolibri.img tmp
sudo cp -rfv $IMG_DIR/* tmp
sudo umount $ISO_DIR/kolibri.img
rm -rf tmp

cd $ISO_DIR
mkisofs -U -J -pad -b kolibri.img -c boot.catalog -hide-joliet boot.catalog -graft-points -o ../kolibri.iso ./
cd ..
