
# Compila os arquivos principais de cada arquivo e coloca na pasta bin
# javac -d bin -sourcepath src \
#     src/architecture/Architecture.java \
#     src/components/Bus.java \
#     src/components/Demux.java \
#     src/components/Memory.java \
#     src/components/Register.java \
#     src/components/Ula.java \
#     src/assembler/Assembler.java 

# Compila todos os arquivos, incluindo os de teste
javac -d bin -sourcepath src -cp lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar $(find src -name "*.java")

# Roda o arquivo principal compilado em bin/architecture/Architecture.class
# Se quiser rodar outro arquivo só mudar o nome do arquivo e pra rodar o de teste utilizar a extensão do vscode
#java -cp bin architecture.Architecture

# Se não funcionar testar:
# java -cp bin:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar architecture.Architecture