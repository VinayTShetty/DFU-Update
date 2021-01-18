package com.dfu.example;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;

public class MainActivity extends AppCompatActivity {

    String deviceAddress="D4:A6:CB:43:B6:70";
    String deviceName="Succorfish SC2";
    boolean keepBond =true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DfuServiceInitiator starter = new DfuServiceInitiator(deviceAddress)
                .setDeviceName(deviceName)
                .setKeepBond(keepBond);
// If you want to have experimental buttonless DFU feature (DFU from SDK 12.x only!) supported call
// additionally:
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
// but be aware of this: https://devzone.nordicsemi.com/question/100609/sdk-12-bootloader-erased-after-programming/
// and other issues related to this experimental service.

// For DFU bootloaders from SDK 15 and 16 it may be required to add a delay before sending each
// data packet. This delay gives the DFU target more time to perpare flash memory, causing less
// packets being dropped and more reliable transfer. Detection of packets being lost would cause
// automatic switch to PRN = 1, making the DFU very slow (but reliable).
        starter.setPrepareDataObjectDelay(300L);

// Init packet is required by Bootloader/DFU from SDK 7.0+ if HEX or BIN file is given above.
// In case of a ZIP file, the init packet (a DAT file) must be included inside the ZIP file.
        if (mFileType == DfuService.TYPE_AUTO)
            starter.setZip(mFileStreamUri, mFilePath);
        else {
            starter.setBinOrHex(mFileType, mFileStreamUri, mFilePath).setInitFile(mInitFileStreamUri, mInitFilePath);
        }
        final DfuServiceController controller = starter.start(this, DfuService.class);
    }
}