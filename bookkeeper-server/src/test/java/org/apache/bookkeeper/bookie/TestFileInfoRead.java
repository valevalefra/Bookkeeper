package org.apache.bookkeeper.bookie;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestFileInfoRead {


    private long start;
    private boolean bestEffort;
    private FileInfo fileInfo;
    private int expectedByte;
    private ByteBuffer bb;
    private ByteBuffer[] writeBuffer;
    private Object expected;
    private static File lf;



    @Parameterized.Parameters
    public static Collection BufferedChannelParameters() throws IOException {
        return Arrays.asList(new Object[][] {

                // Suite minimale
                {ByteBuffer.allocate(6000), 0, true, generateWriteBuffer(4), 5120},
                {ByteBuffer.allocate(6000), 1, false, generateWriteBuffer(7), 6000},
                {null, 1, true, generateWriteBuffer(7), null},
                {ByteBuffer.allocate(0), 8000, true, generateWriteBuffer(7), 0},
                {ByteBuffer.allocate(5), -1, true, generateWriteBuffer(7), 5},

                //Jacoco
                {ByteBuffer.allocate(6000), 8000, false, generateWriteBuffer(7), null}, //capture exepction
        });
    }




    private static Object generateWriteBuffer(int n) {
        ByteBuffer[] buffs=new ByteBuffer[n];
        for(int i=0; i<n;i++) {
            buffs[i] = ByteBuffer.allocate(1024);
        }
        return buffs;
    }




    public TestFileInfoRead(ByteBuffer bb, long start, boolean bestEffort, ByteBuffer[] writeBuffer, Object expected) {

        this.bb=bb;
        this.start=start;
        this.bestEffort=bestEffort;
        this.writeBuffer=writeBuffer;
        this.expected=expected;

    }





    @Before
    public void setUp() throws IOException {

        try {
            lf = new File("filename3.csv");
            if (lf.createNewFile()) {
                System.out.println("File created: " + lf.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        byte[] key="1".getBytes();
        fileInfo = new FileInfo(lf, key, expectedByte);
        fileInfo.write(writeBuffer, 1024);

    }

    @After
    public void delete() throws IOException {

        fileInfo.close(true);
        Path path = Paths.get(lf.getAbsolutePath());
        Files.delete(path);

    }



    @Test
    public void test1() throws IOException {

        int actual;
        long updateStart=start+(writeBuffer.length*1024-start)+1024+1024;

        if(bb==null){
            try{
                fileInfo.read(bb, start, bestEffort);
            }catch(Exception e){
                Assert.assertEquals(expected, e.getMessage());
            }
            return;
        }

        if(((writeBuffer.length*1024+1024)-start<bb.remaining() && bestEffort==true)||(writeBuffer.length*1024+1024)-start>bb.remaining()) {
            actual = fileInfo.read(bb, start, bestEffort);
            Assert.assertEquals(expected, actual);
            return;
        }

        if((writeBuffer.length*1024+1024)-start<bb.remaining() && bestEffort==false) {

            try{fileInfo.read(bb, start, bestEffort);}
            catch (Exception e) {
                Assert.assertEquals("Short read at " + lf.getPath() + "@" + updateStart, e.getMessage());
            }
            return;
        }




    }

    //Introduce second test for coverage fc==null
    @Test
    public void test2() throws IOException {

        int actual;
        byte[] key="1".getBytes();
        File lf = new File("program1.txt");
        fileInfo = new FileInfo(lf, key, expectedByte);
        fileInfo.close(false);
        actual=fileInfo.read(bb, start, bestEffort);
        Assert.assertEquals(0, actual);

    }



}