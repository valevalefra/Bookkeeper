package org.apache.bookkeeper.bookie;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class TestFileInfoWrite {


    private final long position;
    private final int expected;
    private ByteBuffer[] writeBuffer;
    private static File lf;
    private FileInfo fileInfo;

    @Parameterized.Parameters
    public static Collection BufferedChannelParameters() throws IOException {
        return Arrays.asList(new Object[][] {

                // Suite minimale
                {generateWriteBuffer(1), 1024,3072},
                {generateWriteBuffer(2), 1024,4096},
                {generateWriteBuffer(3), 1024,5120},
                {generateWriteBuffer(3), 3072,7168},
                {generateWriteBuffer(3), 0,4096},
                {generateWriteBuffer(3), -5,4091}

                //pit caso in cui non scrivo niente


        });
    }

    private static Object generateWriteBuffer(int n) {
        ByteBuffer[] buffs=new ByteBuffer[n];
        for(int i=0; i<n;i++) {
            buffs[i] = ByteBuffer.allocate(1024);
        }
        return buffs;
    }

    public TestFileInfoWrite( ByteBuffer[] writeBuffer, long position, int expected) {

        this.writeBuffer=writeBuffer;
        this.position=position;
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


        byte[] key = "1".getBytes();
        fileInfo = new FileInfo(lf, key, 1);

    }


    @After
    public void delete() throws IOException {

        //l'ho dovuta chimare altrimenti non mi faceva chiudere il file perchè mi diceva che era in utilizzo
        fileInfo.close(true);
        Path path = Paths.get(lf.getAbsolutePath());
        Files.delete(path);

    }


    @Test
    public void test() throws IOException {

        fileInfo.write(writeBuffer,position);
        Assert.assertEquals(fileInfo.getLf().length(), expected);



    }

    //add for pit, case return 0
    @Test
    public void test1() throws IOException {

        long result;

        ByteBuffer[] buffs=new ByteBuffer[1];
        buffs[0] = ByteBuffer.allocate(0);
        result = fileInfo.write(buffs,position);

        Assert.assertEquals(0,result);
        

    }

    //add for pit, case return 1
    @Test
    public void test2() throws IOException {

        long result;

        ByteBuffer[] buffs=new ByteBuffer[1];
        buffs[0] = ByteBuffer.allocate(1);
        result = fileInfo.write(buffs,position);

        Assert.assertEquals(1,result);


    }

    }




