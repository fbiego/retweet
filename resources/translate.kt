import java.io.File

const val SEPARATOR = "\t"

const val LICENSE = """
<!--
  ~
  ~ MIT License
  ~
  ~ Copyright (c) 2021 Felix Biego
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in all
  ~ copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  ~ SOFTWARE.
  -->

"""

fun main(args: Array<String>) {
    
    if (args.size > 0){
        val data = File(args[0]).readLines()
        if (data.size > 0){
            generate(data)
			println("-----Done-------")
        } else {
			println("Could not read from ${args[0]}")
		}
    } else {
        println("Specify the file name")
    }

    
}

fun generate(strings: List<String>){
    val lang = strings[0].split(SEPARATOR).count() - 1
    println("Languages: $lang")
	
	val dir = File("../app/src/main/res")
	if (!dir.exists())
        dir.mkdirs()
		println("Created output folder")
    
    for (x in 0..lang){
        var output = ""
        var add = ""
    	output += "<resources>\n"
        var name = ""
    	for (s in strings){
        	var word = s.split(SEPARATOR)
            if (word[0] == "id"){
                name = if (word[x] == "en"){
                    "values"
                } else if (word[x] == "id"){
                    "values-dev"
                } else {
                    "values-${word[x]}"
                }
            } else {
                output += "\t<string name=\"${word[0]}\">${word[x]}</string>\n"
                add += "\t<string name=\"${word[0]}\">${word[x]}</string>\n"
            }
    	}
        // output += EXTRA
        add += "</resources>"
    	output += "</resources>"

        output = output.replace("&", "&amp;").replace("\'", "\\\'")
        add = add.replace("&", "&amp;").replace("\'", "\\\'")
		if (!dir.exists()){
			dir.mkdirs()
		}
		val res = File(dir, name)
		if (!res.exists()){
            res.mkdirs()
		}
		val file = File(res, "strings.xml")
        if (file.exists()){
            var txt = file.readText()
            txt = txt.replace( "</resources>", add)
            if (x == 1){
                //println(txt)
            }
            
            // file.writeText(txt)
            file.writeText(LICENSE + output)
        } else {
            file.createNewFile()
		    file.writeText(LICENSE + output)
        }
		
        println("Generated: ${name}/strings.xml")
	}
}
