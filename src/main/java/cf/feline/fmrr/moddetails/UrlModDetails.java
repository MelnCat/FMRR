package cf.feline.fmrr.moddetails;

import com.google.gson.JsonObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class UrlModDetails extends ModDetails {
	final URL url;

	public UrlModDetails(JsonObject obj) {
		super(obj);
		try {
			url = new URL(obj.get("url").getAsString());
		} catch (MalformedURLException e) {
			throw new RuntimeException("Mod " + name + " has malformed URL.", e);
		}
	}

	@Override
	protected byte[] getData() {
		try {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
			try (InputStream stream = conn.getInputStream()) {
				return IOUtils.toByteArray(stream);
			}
		} catch (IOException e) {
			throw new RuntimeException("Mod " + name + " has invalid URL.", e);
		}
	}
}