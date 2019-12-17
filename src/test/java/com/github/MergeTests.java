package com.github;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.example.tutorial.Test.F;
import com.example.tutorial.Test.NestedRecord;
import com.example.tutorial.Test.TestRecord;
import com.google.protobuf.FieldMask;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.google.protobuf.util.FieldMaskUtil;

public class MergeTests {

    @Test
    public void mapMergeWithoutFieldMask() {
        F original = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setName(StringValue.of("Mark")).setAge(Int32Value.of(18)).build()))
                .putAllMap(Map.of("second", F.newBuilder().setName(StringValue.of("Leo")).setAge(Int32Value.of(22)).build()))
                .build();
        F patch = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setAge(Int32Value.of(99)).build()))
                .build();
        F merged = original.toBuilder().mergeFrom(patch).build();
        Assert.assertEquals(99, merged.getMapMap().get("first").getAge().getValue());
        Assert.assertFalse(merged.getMapMap().get("first").hasName());
        Assert.assertEquals(22, merged.getMapMap().get("second").getAge().getValue());
        Assert.assertEquals("Leo", merged.getMapMap().get("second").getName().getValue());
    }

    @Test
    public void mapMergeNotNested() {
        F original = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setName(StringValue.of("Mark")).setAge(Int32Value.of(18)).build()))
                .putAllMap(Map.of("second", F.newBuilder().setName(StringValue.of("Leo")).setAge(Int32Value.of(22)).build()))
                .build();
        F patch = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setAge(Int32Value.of(99)).build()))
                .build();
        F.Builder mergedBuilder = original.toBuilder();
        FieldMaskUtil.merge(FieldMask.newBuilder().addPaths("map").build(), patch, mergedBuilder);
        F merged = mergedBuilder.build();
        Assert.assertEquals(99, merged.getMapMap().get("first").getAge().getValue());
        Assert.assertFalse(merged.getMapMap().get("first").hasName());
        Assert.assertEquals(22, merged.getMapMap().get("second").getAge().getValue());
        Assert.assertEquals("Leo", merged.getMapMap().get("second").getName().getValue());
    }

    @Test
    public void mapMergeNotNestedReplace() {
        F original = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setName(StringValue.of("Mark")).setAge(Int32Value.of(18)).build()))
                .putAllMap(Map.of("second", F.newBuilder().setName(StringValue.of("Leo")).setAge(Int32Value.of(22)).build()))
                .build();
        F patch = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setAge(Int32Value.of(99)).build()))
                .build();
        F.Builder mergedBuilder = original.toBuilder();
        FieldMaskUtil.merge(FieldMask.newBuilder().addPaths("map").build(), patch, mergedBuilder, new FieldMaskUtil.MergeOptions().setReplaceRepeatedFields(true));
        F merged = mergedBuilder.build();
        Assert.assertEquals(99, merged.getMapMap().get("first").getAge().getValue());
        Assert.assertFalse(merged.getMapMap().get("first").hasName());
        // removed Leo because ot replaces whole map
        Assert.assertNull(merged.getMapMap().get("second"));
        Assert.assertNull(merged.getMapMap().get("second"));
    }

    @Test
    public void mapMergeWithFieldMask() {
        F original = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setName(StringValue.of("Mark")).setAge(Int32Value.of(18)).build()))
                .putAllMap(Map.of("second", F.newBuilder().setName(StringValue.of("Leo")).setAge(Int32Value.of(22)).build()))
                .build();
        F patch = F.newBuilder()
                .putAllMap(Map.of("first", F.newBuilder().setAge(Int32Value.of(99)).build()))
                .build();
        F.Builder mergedBuilder = original.toBuilder();
        FieldMaskUtil.merge(FieldMask.newBuilder().addPaths("map.first").build(), patch, mergedBuilder);
        F merged = mergedBuilder.build();
        // no changes as mas don't support nested masks
        Assert.assertEquals(18, merged.getMapMap().get("first").getAge().getValue());
        Assert.assertEquals("Mark", merged.getMapMap().get("first").getName().getValue());
        Assert.assertEquals(22, merged.getMapMap().get("second").getAge().getValue());
        Assert.assertEquals("Leo", merged.getMapMap().get("second").getName().getValue());
    }

    @Test
    public void name() {
        TestRecord original = TestRecord.newBuilder()
                .setAge(Int32Value.of(22))
                .setName(StringValue.of("Mark"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("black"))
                                .setLikes(Int32Value.of(1020))
                                .build()
                )
                .build();
        TestRecord patch = TestRecord.newBuilder()
                .setAge(Int32Value.of(19))
                .setName(StringValue.of("Leo"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("red"))
                                .setLikes(Int32Value.of(500))
                                .build()
                )
                .build();
        TestRecord merged = original.toBuilder().mergeFrom(patch).build();
        Assert.assertEquals(19, merged.getAge().getValue());
        Assert.assertEquals("Leo", merged.getName().getValue());
        Assert.assertEquals(500, merged.getNested().getLikes().getValue());
        Assert.assertEquals("red", merged.getNested().getColor().getValue());
    }

    @Test
    public void name2() {
        TestRecord original = TestRecord.newBuilder()
                .setAge(Int32Value.of(22))
                .setName(StringValue.of("Mark"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("black"))
                                .setLikes(Int32Value.of(1020))
                                .build()
                )
                .build();
        TestRecord patch = TestRecord.newBuilder()
                .setAge(Int32Value.of(19))
                .setName(StringValue.of("Leo"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("red"))
                                .setLikes(Int32Value.of(500))
                                .build()
                )
                .build();
        TestRecord.Builder mergedBuilder = original.toBuilder();
        // no changes cause mask is empty
        FieldMaskUtil.merge(FieldMask.newBuilder().build(), patch, mergedBuilder);
        TestRecord merged = mergedBuilder.build();
        Assert.assertEquals(22, merged.getAge().getValue());
        Assert.assertEquals("Mark", merged.getName().getValue());
        Assert.assertEquals(1020, merged.getNested().getLikes().getValue());
        Assert.assertEquals("black", merged.getNested().getColor().getValue());
    }

    @Test
    public void name3() {
        TestRecord original = TestRecord.newBuilder()
                .setAge(Int32Value.of(22))
                .setName(StringValue.of("Mark"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("black"))
                                .setLikes(Int32Value.of(1020))
                                .build()
                )
                .build();
        TestRecord patch = TestRecord.newBuilder()
                .setAge(Int32Value.of(19))
                .setName(StringValue.of("Leo"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("red"))
                                .setLikes(Int32Value.of(500))
                                .build()
                )
                .build();
        TestRecord.Builder mergedBuilder = original.toBuilder();
        // only top level changed no nested
        FieldMaskUtil.merge(FieldMask.newBuilder().addAllPaths(List.of("age", "name")).build(), patch, mergedBuilder);
        TestRecord merged = mergedBuilder.build();
        Assert.assertEquals(19, merged.getAge().getValue());
        Assert.assertEquals("Leo", merged.getName().getValue());
        Assert.assertEquals(1020, merged.getNested().getLikes().getValue());
        Assert.assertEquals("black", merged.getNested().getColor().getValue());
    }

    @Test
    public void name4() {
        TestRecord original = TestRecord.newBuilder()
                .setAge(Int32Value.of(22))
                .setName(StringValue.of("Mark"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("black"))
                                .setLikes(Int32Value.of(1020))
                                .build()
                )
                .build();
        TestRecord patch = TestRecord.newBuilder()
                .setAge(Int32Value.of(19))
                .setName(StringValue.of("Leo"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("red"))
                                .setLikes(Int32Value.of(500))
                                .build()
                )
                .build();
        TestRecord.Builder mergedBuilder = original.toBuilder();
        // only nested color
        FieldMaskUtil.merge(FieldMask.newBuilder().addAllPaths(List.of("nested.color")).build(), patch, mergedBuilder);
        TestRecord merged = mergedBuilder.build();
        Assert.assertEquals(22, merged.getAge().getValue());
        Assert.assertEquals("Mark", merged.getName().getValue());
        Assert.assertEquals(1020, merged.getNested().getLikes().getValue());
        Assert.assertEquals("red", merged.getNested().getColor().getValue());
    }

    @Test
    public void name5() {
        TestRecord original = TestRecord.newBuilder()
                .setAge(Int32Value.of(22))
                .setName(StringValue.of("Mark"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("black"))
                                .setLikes(Int32Value.of(1020))
                                .build()
                )
                .build();
        TestRecord patch = TestRecord.newBuilder()
                .setAge(Int32Value.of(19))
                .setName(StringValue.of("Leo"))
                .setNested(
                        NestedRecord.newBuilder()
                                .setColor(StringValue.of("red"))
                                .setLikes(Int32Value.of(500))
                                .build()
                )
                .build();
        TestRecord.Builder mergedBuilder = original.toBuilder();
        // nested is merged
        FieldMaskUtil.merge(FieldMask.newBuilder().addAllPaths(List.of("nested")).build(), patch, mergedBuilder);
        TestRecord merged = mergedBuilder.build();
        Assert.assertEquals(22, merged.getAge().getValue());
        Assert.assertEquals("Mark", merged.getName().getValue());
        Assert.assertEquals(500, merged.getNested().getLikes().getValue());
        Assert.assertEquals("red", merged.getNested().getColor().getValue());
    }
}
