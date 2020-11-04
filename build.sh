cd iso
rm kolibri.img
wget https://builds.kolibrios.org/rus/data/data/distribution_kit/kolibri.img
mkisofs -U -J -pad -b kolibri.img -c boot.catalog -hide-joliet boot.catalog -graft-points -o ../kolibri.iso ./
cd ..
