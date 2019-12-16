package com.github;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.example.tutorial.AddressBookProtos;
import com.example.tutorial.Test;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.FieldMask;
import com.google.protobuf.util.FieldMaskUtil;

public class App {

    public static void main(String[] args) throws IOException {
//        AddressBookProtos.AddressBook.Builder addressBook = AddressBookProtos.AddressBook.newBuilder();
//        addressBook.putPeople("first", AddressBookProtos.Person.newBuilder().setEmail("rubin94@gmail.com").build());
//        addressBook.putPeople("second", AddressBookProtos.Person.newBuilder().setEmail("ola94@gmail.com").build());
//        AddressBookProtos.AddressBook book = addressBook.build();
//
//        AddressBookProtos.AddressBook.Builder patchBuilder = AddressBookProtos.AddressBook.newBuilder();
//        patchBuilder.putPeople("second", AddressBookProtos.Person.newBuilder().setEmail("ola94@gmail.com").build());
//        AddressBookProtos.AddressBook patch = patchBuilder.build();
//
//        AddressBookProtos.AddressBook.Builder merged = book.toBuilder();
//        FieldMaskUtil.merge(FieldMask.newBuilder().addPaths("people.second").build(), patch, merged);
//
//        System.out.println(merged);

//        AddressBookProtos.AddressBook.Builder addressBook = AddressBookProtos.AddressBook.newBuilder();
//        addressBook.setOffice(AddressBookProtos.Office.newBuilder()
//                .setPostal("02-789")
//                .setStreet("Hello 2/3")
//                .build());
//        AddressBookProtos.AddressBook book = addressBook.build();
//
//        AddressBookProtos.AddressBook.Builder patchBuilder = AddressBookProtos.AddressBook.newBuilder();
//        patchBuilder.setOffice(AddressBookProtos.Office.newBuilder()
//                .setPostal("02-666")
//                .build());
//        AddressBookProtos.AddressBook patch = patchBuilder.build();
//
//        AddressBookProtos.AddressBook.Builder merged = book.toBuilder();
//        FieldMaskUtil.merge(FieldMask.newBuilder().addPaths("office").build(), patch, merged);
//
//        System.out.println(merged);

        com.example.tutorial.Test.F.Builder f = com.example.tutorial.Test.F.newBuilder();
        f.putB("a", com.example.tutorial.Test.B.newBuilder().setD(1).setX(2).build());
        f.setC(1);
        com.example.tutorial.Test.Main.Builder builder = com.example.tutorial.Test.Main.newBuilder();
        builder.setF(f);

        com.example.tutorial.Test.F.Builder patch = com.example.tutorial.Test.F.newBuilder();
        patch.putB("a", com.example.tutorial.Test.B.newBuilder().setD(10).build());
        patch.putB("b", com.example.tutorial.Test.B.newBuilder().setD(11).build());
        patch.setC(0);
        com.example.tutorial.Test.Main.Builder builderPatch = com.example.tutorial.Test.Main.newBuilder();
        builderPatch.setF(patch);

        System.out.println(builder.build());

        FieldMaskUtil.merge(
                FieldMask.newBuilder().addPaths("f.b").build(),
                builderPatch.build(),
                builder,
                new FieldMaskUtil.MergeOptions().setReplacePrimitiveFields(true)
        );

        System.out.println(builderPatch.build());
        System.out.println(builder.build());

//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        CodedOutputStream output = CodedOutputStream.newInstance(os);
//        patch.writeTo(output);
//        output.flush();
//        os.flush();
//
//        Test.A a1 = Test.A.parseFrom(CodedInputStream.newInstance(new ByteArrayInputStream(os.toByteArray())));
//        System.out.println("done");
//        System.out.println(a1);
    }

}
