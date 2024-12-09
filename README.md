# JKU KUSSS Grade Visualizer

This is a command-line tool that scrapes evaluation results from [KUSSS](https://www.kusss.jku.at/) using [HtmlUnit](https://htmlunit.sourceforge.io/).  
The tool supports different output formats:  
- Text output in the command line.  
- Export as CSV.  
- Export as HTML.  

## Usage

To use the tool, you can either:
1. Download the entire IntelliJ project and build your own `.jar` file.  
2. Download the prebuilt `.jar` file from [here](MissingSemEx1/out/artifacts/MissingSemEx1_jar/MissingSemEx1.jar).

### Output Options

To specify the output format, use the following parameters:

- `-t`  
  Enables text output. If no other flags (`-c` or `-h`) are specified, the program defaults to this option.  
  Examples:
  ```bash
  java -jar program.jar -t  
  ```
  or simply:
  ```bash
  java -jar program.jar
  ```  

- `-c [directory path]`  
  Enables saving the output in CSV format.  
  - Optionally, specify a directory path where the CSV file will be saved.  
  - If no directory is provided, the system's temporary directory will be used as the default.  
  Examples:  
  ```bash
  java -jar program.jar -c /path/to/directory  
  ```
  or:  
  ```bash
  java -jar program.jar -c  
  ```

- `-h [directory path]`  
  Enables saving the output in HTML format.  
  - Optionally, specify a directory path where the HTML file will be saved.  
  - If no directory is provided, the system's temporary directory will be used as the default.  
  Examples:  
  ```bash
  java -jar program.jar -h /path/to/directory  
  ```
  or:  
  ```bash
  java -jar program.jar -h 
  ``` 

### Combinations
You can use multiple options simultaneously. For example:  
```bash
java -jar program.jar -c /csv/path -h /html/path
```

