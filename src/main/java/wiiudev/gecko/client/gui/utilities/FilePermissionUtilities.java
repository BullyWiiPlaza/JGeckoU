package wiiudev.gecko.client.gui.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilePermissionUtilities
{
	public static void giveAllPermissionsFor(Path extractedFile) throws IOException
	{
		Set<PosixFilePermission> permissions = new HashSet<>();
		permissions.addAll(Arrays.asList(PosixFilePermission.values()));
		Files.setPosixFilePermissions(extractedFile, permissions);
	}
}