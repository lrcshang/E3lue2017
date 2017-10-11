package com.e3lue.us.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Enzate on 2017/10/11.
 */

public class Pingutil {
    /// host = 192.168.8.8

    /// pingcount = 2
    public static boolean ping(String host, int pingCount) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
        String command = "ping -c " + pingCount + " " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {

                return false;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {

            }
            int status = process.waitFor();
            if (status == 0) {

                isSuccess = true;
            } else {

                isSuccess = false;
            }

        } catch (IOException e) {

        } catch (InterruptedException e) {

        } finally {

            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {

                }
            }
        }
        return isSuccess;
    }

}
