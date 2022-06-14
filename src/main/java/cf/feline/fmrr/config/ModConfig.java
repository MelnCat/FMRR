package cf.feline.fmrr.config;

import cf.feline.fmrr.moddetails.ModDetails;
import cf.feline.fmrr.moddetails.UrlModDetails;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ModConfig {
	private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("fmrr.json");
	private static final Gson gson = new GsonBuilder()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.create();

	public static ModConfig getConfig() {
		try {
			if (!Files.exists(FILE)) {
				Files.createFile(FILE);
				ModConfig nc = new ModConfig();
				Files.write(FILE, gson.toJson(nc).getBytes(StandardCharsets.UTF_8));
			}
			JsonObject data = gson.fromJson(Files.newBufferedReader(FILE), JsonObject.class);
			ModConfig cfg = new ModConfig();
			ArrayList<ModDetails> details = new ArrayList<>();
			data.get("mods").getAsJsonArray().iterator().forEachRemaining(
				o -> {
					ModDetails d;
					if (!o.isJsonObject()) return;
					JsonObject obj = o.getAsJsonObject();
					String type = obj.get("type").getAsString();
					if (type.equals("url")) d = new UrlModDetails(obj);
					else throw new RuntimeException(new IllegalStateException("Invalid mod type " + type + "."));
					details.add(d);
				}
			);
			cfg.mods = details;
			cfg.remove = StreamSupport.stream(data.get("remove").getAsJsonArray().spliterator(), false)
				.map(JsonElement::getAsString).collect(Collectors.toList());
			return cfg;
		} catch (IOException e) {
			throw new RuntimeException("Error while loading config file.", e);
		}
	}

	public List<ModDetails> mods = Collections.emptyList();
	public List<String> remove = Collections.emptyList();
}
