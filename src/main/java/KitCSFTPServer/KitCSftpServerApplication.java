package KitCSFTPServer;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.io.IoServiceFactoryFactory;
import org.apache.sshd.mina.MinaServiceFactoryFactory;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

@SpringBootApplication
public class KitCSftpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KitCSftpServerApplication.class, args);
	}

	// -- Method to be called in the main Class that will create and start a new SFTP Server
	@EventListener(ApplicationReadyEvent.class)
	public void SFTPServer() throws IOException {
		System.setProperty(IoServiceFactoryFactory.class.getName(), MinaServiceFactoryFactory.class.getName());
		SshServer sshd = SshServer.setUpDefaultServer();

		// -- Sets the port of the server to 2222. Client application must use specified port
		sshd.setPort(2222);

		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File("host.ser").toPath()));

		sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));

		sshd.setCommandFactory(new ScpCommandFactory());

		// -- Sets the location of the home directory of the server
		VirtualFileSystemFactory vfSysFactory = new VirtualFileSystemFactory();
		vfSysFactory.setDefaultHomeDir(new File("/home/bkite/KitCServer").toPath());
		sshd.setFileSystemFactory(vfSysFactory);

		// -- Sets the username and password for the session. Client application must use specified login information
		sshd.setPasswordAuthenticator((username, password, session) ->
				username.equals("test") && password.equals("password"));

		//sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(new File("src/main/resources/AuthorizedKeys").toPath()));

		// -- Starts the SFTP Server
		sshd.start();
	}
}
