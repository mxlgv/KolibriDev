#!/bin/bash
MACHINENAME="KolibriDE"
# Создать ВМ
status=$(VBoxManage list vms | grep "KolibriDE")
if [ -z "$status" ]
then
    echo "Creating a virtual machine..."
    VBoxManage createvm --name $MACHINENAME --ostype "Linux32" --register
    # Установить память и сеть
    VBoxManage modifyvm $MACHINENAME --ioapic on
    VBoxManage modifyvm $MACHINENAME --memory 1024 --vram 128
    VBoxManage modifyvm $MACHINENAME --nic1 nat
    # Подключить  Iso

    VBoxManage storagectl $MACHINENAME --name " IDE Controller " --add ide --controller PIIX4
    VBoxManage storageattach $MACHINENAME --storagectl " IDE Controller " --port 1 --device 0 --type dvddrive --medium kolibri.iso
    VBoxManage modifyvm $MACHINENAME --boot1 dvd --boot2 disk --boot3 none --boot4 none
fi
# Запускаем виртуальную машину
VBoxManage startvm $MACHINENAME 
