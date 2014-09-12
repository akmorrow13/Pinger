all: Client.class Server.class Pinger.class ClientReceiver.class

%.class: %.java

	javac -d . -classpath . $<

clean:

	rm -f *.class 