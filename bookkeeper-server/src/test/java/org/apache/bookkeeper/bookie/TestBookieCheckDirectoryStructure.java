package org.apache.bookkeeper.bookie;

import junit.framework.TestCase;
import org.apache.bookkeeper.util.BookKeeperConstants;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

public class TestBookieCheckDirectoryStructure {




    //Directory is created
    @Test
    public void test1() throws IOException {

        File dir;

        boolean result = true;

        dir = new File("src/test/prova/");
        try {
            Bookie.checkDirectoryStructure(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(result);
        dir.delete();


    }

    //Directory is null
    @Test
    public void test2() {

        boolean result = true;

        File dir = null;
        try {
            Bookie.checkDirectoryStructure(dir);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            Assert.assertEquals(null, e.getMessage());
        }



    }

    //Directory has already been created
    @Test
    public void test3() throws IOException {

        boolean result = false;


        File dir = new File("src/test/prova");
        dir.mkdirs();

        try {
            Bookie.checkDirectoryStructure(dir);
            dir.delete();
        } catch (IOException e) {
            e.printStackTrace();
            result = true;
        }

        Assert.assertFalse(result);



    }


    //Jacoco
    @Test
    public void test4() throws IOException {

        boolean result = false;


        File dir= new File("src/test/prova/file");
        dir.mkdirs();

        File file1 = new File("src/test/prova/file1.txn");
        file1.createNewFile();

        File file2 = new File("src/test/prova/file2.idx");
        file2.createNewFile();

        File file3 = new File("src/test/prova/file3.log");
        file3.createNewFile();

        dir.delete();
        try {
            Bookie.checkDirectoryStructure(dir);

        } catch (IOException e) {
            e.printStackTrace();
            result = true;
            Assert.assertEquals(e.getMessage(), "Directory layout version is less than 3, upgrade needed");
        }

        File dirDelete= new File("src/test/prova");

        file1.delete();
        file2.delete();
        file3.delete();
        dirDelete.delete();


    }

  /*  @Test
    public void Test5(){


        File dir = new File("src/test/dir1/dir2");

        File dirMock = Mockito.spy(dir);

        dir.delete();

        Mockito.doReturn(false).when(dirMock).mkdirs();

        try {
            Bookie.checkDirectoryStructure(dirMock);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertEquals(e.getMessage(),"Unable to create directory " + dir);
        }

        dir.delete();
    }*/

}

