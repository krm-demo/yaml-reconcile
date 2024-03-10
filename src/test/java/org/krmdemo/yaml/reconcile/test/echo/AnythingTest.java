package org.krmdemo.yaml.reconcile.test.echo;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnythingTest {

    boolean isMountArray(int [] array) {
        if (array == null || array.length < 3) {
            throw new IllegalArgumentException("Not possible to detect whether it's mount");
        }
        int countIncreasing = 0;
        int countDecreasing = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] == array[i-1]) {
                continue;
            } else if (array[i] > array[i-1]) {
                countIncreasing++;
                if (countDecreasing > 0) {
                    return false;
                }
            } else if (array[i] < array[i-1]) {
                countDecreasing++;
                if (countIncreasing == 0) {
                    return false;
                }
            }
        }
        return (countIncreasing > 0) && (countDecreasing > 0);
    }

    @Test
    void test() {
        assertFalse(isMountArray(new int[]{1, 1, 1}));
        assertFalse(isMountArray(new int[]{1, 5, 12}));
        assertTrue(isMountArray(new int[]{1, 15, 12}));
        assertTrue(isMountArray(new int[]{0,3,5,6,8,12,9,4,3,1, 0, -1, }));
        assertFalse(isMountArray(new int[]{0,3,5,6,8,12,9,4,3,1, 0, -1, 5}));
    }

    static class Record {
        Long id;
        String name;
        String area;
        Double value;
        public static Record fromString(String line) {
            String[] values = line.split(",");
            Record r = new Record();
            if (values.length > 3) {
                r.value = Double.valueOf(values[3]);
            }
            if (values.length > 2) {
                r.area = values[2];
            }
            if (values.length > 1) {
                r.name = values[1];
            }
            if (values.length > 1) {
                r.id = Long.valueOf(values[0]);
            }
            return r;
        }

        @Override
        public String toString() {
            return "Record{" +
                   "id=" + id +
                   ", name='" + name + '\'' +
                   ", area='" + area + '\'' +
                   ", value=" + value +
                   '}';
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Record record = (Record) o;

            if (!Objects.equals(id, record.id)) return false;
            if (!Objects.equals(name, record.name)) return false;
            if (!Objects.equals(area, record.area)) return false;
            return Objects.equals(value, record.value);
        }
    }

    @Test
    void testDuplications() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/a.txt");
        List<String> lines = IOUtils.readLines(inputStream);
        System.out.println(lines);
        List<Record> listRecords = lines.stream().map(Record::fromString).toList();
        System.out.println("listRecords.size() = " + listRecords.size());
        System.out.println("listRecords --> " + listRecords);
        Set<Record> setRecords = new HashSet<>(listRecords);
        System.out.println("setRecords.size = " + setRecords.size());
        System.out.println("setRecords --> " + setRecords);
        assertThat(setRecords.size()).isEqualTo(2);
    }
}
