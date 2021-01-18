package com.dfu.example;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;

public class MainActivity extends AppCompatActivity {

    String deviceAddress="D4:A6:CB:43:B6:70";
    String deviceName="Succorfish SC2";
    boolean keepBond =true;
    Button helloworld,startDFU;
    Intent intent ;
    String mFilePath="/document/primary:nrf/nrf_loopback_dfu.zip";
    int mFileType=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helloworld=(Button)findViewById(R.id.hello_worls);
        startDFU=(Button)findViewById(R.id.startDFU);
        helloworld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 7);
            }
        });

        startDFU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDFUupload();
            }
        });
    }

    public void startDFUupload(){
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
            starter.setZip(Uri.fromFile(new File(mFilePath)), mFilePath);
        else {
          //  starter.setBinOrHex(mFileType, mFileStreamUri, mFilePath).setInitFile(mInitFileStreamUri, mInitFilePath);
        }
        final DfuServiceController controller = starter.start(this, DfuService.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){

            case 7:

                if(resultCode==RESULT_OK){

                    String PathHolder = data.getData().getPath();

                    Toast.makeText(MainActivity.this, PathHolder , Toast.LENGTH_LONG).show();
                    System.out.println("DFU_TAG fileType= = "+getMimeType(PathHolder));
                    System.out.println("DFU_TAG URI "+         Uri.fromFile(new File(PathHolder)));
                    System.out.println("DFU_TAG string path "+         PathHolder);

                }
                break;

        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


}