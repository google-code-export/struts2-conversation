package com.google.code.rees.scope.testutil;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationTestingUtil {

    @SuppressWarnings("unchecked")
    public static <TargetClass> TargetClass getSerializedCopy(TargetClass target)
            throws IOException, ClassNotFoundException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(target);
        oos.close();
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0);

        // deserialize
        InputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(in);
        Object o = ois.readObject();

        return (TargetClass) o;
    }

}
