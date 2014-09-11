all: Client.class Server.class Pinger.class

%.class: %.java

	javac -d . -classpath . $<

clean:

	rm -f *.class 