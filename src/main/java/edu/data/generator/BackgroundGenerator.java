package edu.data.generator;

import edu.data.generator.config.Config;
import edu.data.generator.util.ImageUtil;
import edu.data.generator.util.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class BackgroundGenerator {
    private final static Logger LOG = LoggerFactory.getLogger(BackgroundGenerator.class);

    private final List<BufferedImage> backgrounds;
    private List<String> bg_names;
    private final Random random;
    private boolean initialized;

    public BackgroundGenerator() {
        random = new Random();
        backgrounds = new ArrayList();
        bg_names = new ArrayList();
    }

    public void init() {
        readBackgrounds();
        // fetchBaseXmls();
        initialized = true;
    }

    public List<String> getBgNames(){
        return bg_names;
    }

    public int getRandomBackgroundIndex() {
        if (!initialized) {
            init();
        }
        return random.nextInt(backgrounds.size());
    }

    public BufferedImage getBackgroundAtIdx(final int index) {
        BufferedImage bgImg = backgrounds.get(index);
        String bgImgName = bg_names.get(index);
        LOG.info("Overlaying seeds onto {}", bgImgName);
        return ImageUtil.cloneImage(bgImg, false);
    }

    public double getBackgroundGrainScaleFactorAtIdx(final int index) {
        final String name = bg_names.get(index);
        final int l = name.length();
        final double scale = Double.parseDouble(name.substring(l - 8, l - 4));
        BufferedImage image = getBackgroundAtIdx(index);
        float largestSide = Math.max(image.getWidth(), image.getHeight());
        double grainScale = largestSide * scale;
        LOG.info("Grain Scale Factor Retrieved! Grain length: {} px", grainScale);
        return grainScale;
    }

    private void readBackgrounds(){
        LOG.info("Loading and scaling backgrounds, please wait. This process can take some time...");
        int i = 0;
        Path path = Paths.get(Config.BACKGROUND_DIR);
        long lineCnt = 0;
        lineCnt = path.toFile().listFiles().length;
        
        for (final File file : Reader.listFilesFromDir(Config.BACKGROUND_DIR)){
            i++;
            double pc = Math.round((double)10000 * i/lineCnt)/100.0;
            BufferedImage bfImage = ImageUtil.readImage(file);
            double AR = ((double) bfImage.getHeight() / (double) bfImage.getWidth());
            System.out.printf("Loading and rescaling background %s (AR: %3.2f) - %4.2f%%\n", file.getName(), AR, pc);
            backgrounds.add(ImageUtil.progressiveScaleImage(bfImage, Config.IMAGE_WIDTH, Config.IMAGE_HEIGHT));
            bg_names.add(file.getName());
        }
        LOG.info("{} backgrounds to process.", backgrounds.size());
    }
}
