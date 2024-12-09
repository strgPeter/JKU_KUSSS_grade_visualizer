# JKU KUSSS grade visualizer
This is a commandline tool that scrapes evaluation results from [KUSSS](https://www.kusss.jku.at/) using [HtmlUnit](https://htmlunit.sourceforge.io/).
The tool allows for different output types:
- Text form in commandline
- Expost as CSV
- Export as HTML
## Usage
To use the tool you can either download the entire Intellij Project an build your own .jar file, or you can just download the prebuilt .jar [file](out\artifacts\MissingSemEx1_jar\MissingSemEx1.jar).
To choose between output type you can use the following parameters:
- t: 
Enables text output. If no other flags (-c or -h) are specified, the program defaults to this option.
```bash
java -jar program.jar -t
```
or
```bash
java -jar program.jar
```
- c [directory path]:
Enables saving the output in CSV format. Optionally, you can specify a directory path where the CSV file will be saved. If no directory is provided, the system's temporary directory will be used as the default.
```bash
java -jar program.jar -c /path/to/directory
```
or
```bash
java -jar program.jar -c
```
- h [directory path]:
Enables saving the output in HTML format. Optionally, you can specify a directory path where the HTML file will be saved. If no directory is provided, the system's temporary directory will be used as the default.
```bash
java -jar program.jar -h /path/to/directory
```
or
```bash
java -jar program.jar -h
```
- Combinations:
```bash
java -jar program.jar -c /csv/path -h /html/path
```
