package cf.feline.fmrr.locator;

import cf.feline.fmrr.config.ModConfig;
import cf.feline.fmrr.moddetails.ModDetails;
import com.google.gson.Gson;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.AbstractJarFileLocator;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileParser;
import net.minecraftforge.forgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModLocator extends AbstractJarFileLocator {
	private static final Path FOLDER = FMLPaths.MODSDIR.get();
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public List<IModFile> scanMods() {
		ModConfig cfg = ModConfig.getConfig();
		for (String remove : cfg.remove) {
			Path file = FOLDER.resolve(remove + ".jar");
			try {
				Files.deleteIfExists(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (ModDetails mod : cfg.mods) {
			Path file = mod.getFile();
			try {
				if (Files.exists(file)) {
					byte[] hashed = MessageDigest.getInstance("MD5").digest(Files.readAllBytes(file));
					if (!Arrays.equals(hashed, mod.md5)) {
						LOGGER.info("File {} failed integrity check. Re-downloading...", file.toString());
						Files.delete(file);
					}
				}
				Files.write(file, mod.getModData());
				LOGGER.info("Creating file {}.", file.toString());
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}
		}
		return cfg.mods.stream().map(x -> ModFile.newFMLInstance(x.getFile(), this))
			.peek(f -> modJars.compute(f, (mf, fs) -> createFileSystem(mf)))
			.collect(Collectors.toList());

	}

	@Override
	public String name() {
		return "fmrr";
	}

	@Override
	public void initArguments(Map<String, ?> arguments) {
	}
}
