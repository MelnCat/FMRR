package cf.feline.fmrr.moddetails;

import com.google.gson.JsonObject;
import net.minecraftforge.fml.loading.FMLPaths;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public abstract class ModDetails {
	private static final Path FOLDER = FMLPaths.MODSDIR.get();
	public final String name;

	@Nullable
	public final byte[] md5;

	protected abstract byte[] getData();

	public final byte[] getModData() {
		byte[] data = getData();
		if (md5 != null) {
			try {
				byte[] hash = MessageDigest.getInstance("MD5").digest(data);
				if (!Arrays.equals(md5, hash)) throw new IllegalStateException("Mod " + name + " failed MD5 integrity check.");
			} catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
		}
		return data;
	}

	public Path getFile() {
		return FOLDER.resolve(name + ".jar");
	}

	protected ModDetails(JsonObject obj) {
		name = obj.get("name").getAsString();
		byte[] hash = null;
		try {
			if (obj.has("md5")) hash = Hex.decodeHex(obj.get("md5").getAsString().toCharArray());
		} catch (DecoderException e) {
			LogManager.getLogger().warn("Mod {} has invalid sha256 hash.", name);
		}
		md5 = hash;
	}
}
