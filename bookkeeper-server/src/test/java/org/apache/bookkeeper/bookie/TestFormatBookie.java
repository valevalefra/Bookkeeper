package org.apache.bookkeeper.bookie;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class TestFormatBookie {


   private final ServerConfiguration serverConf;
    private final Boolean isInteractive;
    private final Boolean force;
    private final Object expected;
    private final Boolean setJournal;
    private File dir;
    private File file1;


   public TestFormatBookie(ServerConfiguration serverConf, Boolean isInteractive, Boolean force, Boolean setJournal, Object expected) {

        this.serverConf=serverConf;
        this.isInteractive=isInteractive;
        this.force=force;
        this.setJournal=setJournal;
        this.expected=expected;

    }

    @Parameterized.Parameters
    public static Collection Parameters() throws IOException {


        return Arrays.asList(new Object[][] {



                // Suite minimale
                {null, true,true,false,NullPointerException.class }, //ritornano eccezione
                {null, true,false,false,NullPointerException.class},
                {null, false,false,false,NullPointerException.class},
                {new ServerConfiguration(), false,true,true,true}, //va a buon fine con isInteractive = false, se metto true va in loop
                {new ServerConfiguration(), false,false,true,false}, //bookie format aborted
                {new ServerConfiguration(), false,true,false,true}, //server Configuration without setJournal
                {new ServerConfiguration(), false,false,false,true} //ritorna lo stesso di quello sopra


        });
    }





   @Test
    public void test1() {

        System.out.println("pppp1");
        Object result;
        if (setJournal == true && serverConf != null) {


            serverConf.setAdvertisedAddress("127.0.0.1");
            serverConf.setBookiePort(2182);

            String[] journalDir = new String[]{"dir"};
            serverConf.setJournalDirsName(journalDir);
            dir = new File("dir");
            dir.mkdir();
            file1 = new File(dir, "test.txt");
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (setJournal == false && serverConf != null) {

            serverConf.setAdvertisedAddress("127.0.0.1");
            serverConf.setBookiePort(2182);
        }

        try {
            System.out.println("pppp");
            result = Bookie.format(serverConf, isInteractive, force);

        } catch (Exception e) {
            System.out.println("heyyy");
            result = e.getClass();

        }

        Assert.assertEquals(expected, result);
        if (setJournal == true) {
            dir.delete();
            file1.delete();
        }

    }





}


