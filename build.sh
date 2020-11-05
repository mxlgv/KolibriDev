#!/bin/bash
IMG_DIR=build_img
ISO_DIR=build_iso

# BUILD IMG
echo "====Build IMG===="
./build_img.sh

# BUILD ISO
echo "====Build ISO===="
cd $ISO_DIR
mkisofs -U -J -pad -b kolibri.img -c boot.catalog -hide-joliet boot.catalog -graft-points -o ../kolibri.iso ./
cd ..


# THIS FILE WRITED BY MAXCODEHACK
