package com.jzh.raft.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class raftCoreApplication {
    public static void main(String[] args) {
        int[][] intervals = new int[][]{{1,3},{0,2},{2,3},{4,6},{4,5},{5,5},{0,2},{3,3}};
        int[][] result = merge(intervals);
        System.out.println(result.toString());
    }

    public static int[][] merge(int[][] intervals) {
        if (intervals.length == 1) {
            return intervals;
        }

        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));

        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < intervals.length;) {
            int start = intervals[i][0];
            int end = intervals[i][1];
            int j = i + 1;
            while (j < intervals.length) {
                if (intervals[j][0] > end) {
                    break;
                }
                end = Math.max(end, intervals[j][1]);
                j++;
            }
            i = j;
            int[] item = new int[]{start, end};
            result.add(item);
        }
        return result.toArray(new int[result.size()][]);
    }
}


