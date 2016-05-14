/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package masterPanel;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Viky
 */
public class SingleInst {

    private static final String LOCK_FILEPATH = "Server.lock";// System.getProperty("java.io.tmpdir") + File.separator + "Server.lock";
    private static final File lock = new File(LOCK_FILEPATH);
    private static boolean locked = false;

    private SingleInst() {}
    
    public static boolean lock() throws IOException {
        if(locked) return true;

        if(lock.exists()) return false;
        lock.createNewFile();
        lock.deleteOnExit();
        locked = true;
        return true;
    }
}
