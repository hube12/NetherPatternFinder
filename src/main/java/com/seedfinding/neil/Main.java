package com.seedfinding.neil;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mccore.block.Block;
import com.seedfinding.mccore.block.Blocks;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcterrain.TerrainGenerator;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static final MCVersion VERSION = MCVersion.v1_12;

    public static final ArrayList<BPos> POSITIONS = new ArrayList<BPos>() {{
        add(new BPos(48672,52,47536));
        add(new BPos(48673,51,47536));
        add(new BPos(48674,51,47536));
        add(new BPos(48675,50,47536));
        add(new BPos(48676,49,47536));
        add(new BPos(48672,51,47537));
        add(new BPos(48673,51,47537));
        add(new BPos(48674,50,47537));
        add(new BPos(48675,49,47537));
        add(new BPos(48676,48,47537));
        add(new BPos(48672,51,47538));
        add(new BPos(48673,50,47538));
        add(new BPos(48674,49,47538));
        add(new BPos(48675,48,47538));
        add(new BPos(48676,47,47538));
        add(new BPos(48672,50,47539));
        add(new BPos(48673,49,47539));
        add(new BPos(48674,48,47539));
        add(new BPos(48675,47,47539));
        add(new BPos(48676,46,47539));
        add(new BPos(48672,50,47540));
        add(new BPos(48673,49,47540));
    }};

    public static void main(String[] args) throws Exception {
//        test();
//        if (true) {
//            return;
//        }
//		LongStream.range(0, 1L << 48).parallel().forEach(Main::kernel);
        BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get("./out.txt")));
        List<Long> seeds = reader.lines().map(Long::parseLong).collect(Collectors.toList());
        System.out.printf("Running on %d seeds\n", seeds.size());
        seeds.stream().parallel().forEach(Main::kernel);
        System.out.println("Done, press enter key to exit");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static void test() {
        long worldseed = 42;
        POSITIONS.clear();
        BiomeSource biomeSource = BiomeSource.of(Dimension.NETHER, VERSION, worldseed);
        TerrainGenerator generator = TerrainGenerator.of(Dimension.NETHER, biomeSource);
        int minHeight = 30;
        int offsetX = -8;
        int offsetY = -2;
        for (int x = 0; x < 5; x++) {

            for (int z = 0; z < 5; z++) {
                Block[] column = generator.getColumnAt(offsetX + x, offsetY + z);
                boolean placed = false;
                for (int y = minHeight; y < generator.getMaxWorldHeight() - 1; y++) {
                    if (column[y]==Blocks.NETHERRACK && Block.IS_AIR.test(VERSION, column[y + 1])) {
                        POSITIONS.add(new BPos(offsetX + x, y, offsetY + z));
                        System.out.print(y + " ");
                        placed = true;
                        break;
                    }
                }
                if (!placed)
                    System.out.print("U ");
            }
            System.out.println();
        }
        kernel(worldseed);
    }


    public static void kernel(long worldseed) {
        BiomeSource biomeSource = BiomeSource.of(Dimension.NETHER, VERSION, worldseed);
        TerrainGenerator generator = TerrainGenerator.of(Dimension.NETHER, biomeSource);
        for (BPos pos : POSITIONS) {
            if (pos == null) continue;
            Block[] blocks = generator.getColumnAt(pos.getX(), pos.getZ());
            if (blocks.length < pos.getY()) {
                System.err.printf("Critical error, data inputted is above the range 0-%d (was %d)", blocks.length, pos.getY());
                System.exit(-1);
            }
            Block airBlock = blocks[pos.getY()+1];
            Block netherackBlock = blocks[pos.getY()];
            if (!Block.IS_AIR.test(VERSION, airBlock) || netherackBlock != Blocks.NETHERRACK) {
                return;
            }
        }
        System.out.printf("Seed %d works in version %s%n", worldseed, VERSION);
    }
}
