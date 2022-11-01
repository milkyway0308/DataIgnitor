package skywolf46.dataignitor

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.representer.Representer
import skywolf46.dataignitor.cache.ResourceCacheDownloader
import skywolf46.dataignitor.data.SchemaErrorInfo
import skywolf46.dataignitor.data.SharedResourceIndex
import skywolf46.dataignitor.data.StaticDataSchema
import skywolf46.dataignitor.loader.SchemaDataLoader
import skywolf46.dataignitor.util.CommandLineParser
import skywolf46.dataignitor.util.DataSizeUtil
import skywolf46.dataignitor.util.YamlReader
import skywolf46.dataignitor.util.printError
import java.io.DataInputStream
import java.io.File
import java.util.Properties
import kotlin.system.exitProcess

object DataIgnitor {
    private val parser = CommandLineParser()
    private var server = "http://resources.eveonline.com/"
    private lateinit var targetFileName: String
    private var schemaFileName: String? = null
    private val fileIndexes = mutableMapOf<String, SharedResourceIndex>()
    private var allowDangerousOption = false
    private var loadFromCommandLine = false
    private var doVisualInitialize = false
    private var indexFile: File? = null
    private var cacheLocation: File = File("./caches")
    private var fileBuffer = 1024L * 64

    @JvmStatic
    fun main(args: Array<String>) {
        println("DataIgnitor - EVE Static Data Restorer")
        initAction()
        parseCommandLine(args)
        loadConfiguration()
        summarize()
        initialize()
        process()
    }

    private fun initAction() {
        parser
            .registerAction("cinit", "commandInit") {
                println("Commandline initialization enabled.")
                println("Configuration disabled.")
                loadFromCommandLine = true
            }
            .registerAction("index") {
                val fileLoc = it.joinToString(" ")
                if (!File(fileLoc).exists() || !File(fileLoc).isFile) {
                    printError("Index file not exists at target directory.")
                    exitProcess(-1)
                }
                println("Index file location set to:")
                println(File(fileLoc).absolutePath)
                indexFile = File(fileLoc)
            }
            .registerAction("cache") {
                val directoryLoc = File(it.joinToString(" "))
                if (directoryLoc.exists() && directoryLoc.isFile) {
                    printError("Invalid cache directory : Target path is file.")
                    exitProcess(-1)
                }
                println("Cache file directory set to: ")
                println(directoryLoc.absolutePath)
                cacheLocation = directoryLoc
            }
            .registerAction("buffer", "fb", "bf", "filebuffer") {
                if (it.size != 1) {
                    printError("Cannot set buffer size : Too ${if (it.isEmpty()) "few" else "many"} arguments")
                    exitProcess(-1)
                }
                fileBuffer = it[0].replace(",", "").toLongOrNull() ?: kotlin.run {
                    printError("Cannot set buffer size : Parameter \"${it[0]}\" is probably not number")
                    exitProcess(-1)
                }
                if (fileBuffer >= Integer.MAX_VALUE) {
                    printError("Cannot set buffer size : Buffer size too high")
                    exitProcess(-1)
                }
                val systemMemory = Runtime.getRuntime().maxMemory()
                if (!allowDangerousOption && fileBuffer >= systemMemory / 2) {
                    printError("Cannot set buffer size : Buffer size is higher than half of system memory")
                    printError("To avoid this error, decrease file buffer size or use -disableSafetyLock flag")
                    exitProcess(-1)
                }
                if (fileBuffer <= 32) {
                    printError("Cannot set buffer size : Min buffer size is 32B")
                    exitProcess(-1)
                }
                println("File buffer set to: ${DataSizeUtil.compact(fileBuffer)}")
            }
            .registerAction("schema") {
                schemaFileName = it.joinToString(" ")
                if (!allowDangerousOption && !schemaFileName!!.endsWith(".schema")) {
                    printError("Cannot set schema file : Schema file extension is not \".schema\"")
                    printError("To avoid this error, specify correct schema file or use -disableSafetyLock flag")
                    exitProcess(-1)
                }
            }
            .registerAction("server") {
                if (it.isEmpty()) {
                    printError("Cannot set resource server URL : URL is empty")
                    exitProcess(-1)
                }
                server = it.joinToString(" ").let { url ->
                    if (!url.endsWith('/'))
                        "$url/"
                    else url
                }
                println("EVE Resource server changed to: $server")
            }
            .registerAction("disableSafetyLock") {
                allowDangerousOption = true
                println("Safety lock released")
                println("CAUTION: This operation can cause critical error to local machine")
            }
            .registerAction("output", "opt") {
                when (it[0].lowercase()) {

                }
            }
    }


    private fun parseCommandLine(args: Array<String>) {
        val file = parser.execute(args).joinToString(" ")
        if (file.isEmpty()) {
            printError("Cannot process file : No EVE file specified.")
            exitProcess(-1)
        }
        setTargetFile(file)
    }

    private fun setTargetFile(file: String) {
        if (!allowDangerousOption && !file.endsWith(".static")) {
            printError("Cannot set EVE file : EVE file extension is not \".static\"")
            printError("To avoid this error, specify correct EVE file or use -disableSafetyLock flag")
            exitProcess(-1)
        }
        targetFileName = file
    }

    private fun loadConfiguration() {
        if (loadFromCommandLine) {
            return
        }
        println("Looking for configuration..")
        val file = File(System.getenv("APPDATA"), "DataIgnitor/configuration.properties")
        val properties = Properties()
        if (!file.exists()) {
            println("No configuration found! Creating data file at : ")
            println(file.absolutePath)
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writer().use {
                properties.store(it, "DataIgnitor configuration file")
            }
        }
        println("Loading configurations from :")
        println(file.absolutePath)
    }


    private fun summarize() {
        println("-- Summary: ")
        println("Current EVE resource server is $server.")
        println("Index file is \"${indexFile!!.absolutePath}\".")
        println("Cache file directory is \"${cacheLocation.absolutePath}\".")
        println("File buffer is ${DataSizeUtil.compact(fileBuffer)}.")
        if (allowDangerousOption) {
            println("Safety lock released.")
        }
    }

    private fun initialize() {
        println("-- Initializing")
        SchemaDataLoader.init()
        createCacheFolder()
        parseIndexFile()
    }

    private fun createCacheFolder() {
        println("Validating cache directory..")
        if (!cacheLocation.exists() && !cacheLocation.mkdirs()) {
            printError("Invalid cache directory : Cannot create directory to target path.")
            exitProcess(-1)
        }
    }

    private fun parseIndexFile() {
        println("Parsing index file..")
        val started = System.currentTimeMillis()
        indexFile!!.readLines().forEach {
            val index = SharedResourceIndex.fromIndexFileRow(it)
            fileIndexes[index.fullPath] = index
        }
        println("Loaded ${fileIndexes.size} indexes in ${System.currentTimeMillis() - started}ms")
    }


    private fun process() {
        if (schemaFileName == null) {
            println("No schema file declared from command line.")
            println("DataIgnitor will try schema extract from static data resource file.")
        } else {
            if (!fileIndexes.containsKey(schemaFileName)) {
                printError("Cannot process schema file : Schema file \"${schemaFileName}\" not found at indexes")
                exitProcess(-1)
            }
            println("Schema file declared : $schemaFileName")
        }
        if (!fileIndexes.containsKey(targetFileName)) {
            Thread.sleep(1L)
            System.err.println("Cannot process target file : Target file \"${targetFileName}\" not found at indexes")
            exitProcess(-1)
        }
        println("Downloading target file..")
        ResourceCacheDownloader.downloadIfNotExists(server, fileIndexes[targetFileName]!!, cacheLocation, fileBuffer)
        if (schemaFileName != null) {
            println("Downloading schema file..")
            ResourceCacheDownloader.downloadIfNotExists(
                server,
                fileIndexes[schemaFileName]!!,
                cacheLocation,
                fileBuffer
            )
            fileIndexes[schemaFileName]!!.toSubDirectory(cacheLocation).inputStream().use {
                println(YamlReader(it).root.getKeys(true))
            }
        }
        println("Starting data process for target file \"$targetFileName\"")
        val index = fileIndexes[targetFileName]!!
        val schema = fileIndexes[schemaFileName]
        val errLog = SchemaErrorInfo(null, null)
        index.toSubDirectory(cacheLocation).inputStream().use {
            index.toSubDirectory(cacheLocation, ".yml").writeText(
                Yaml(Representer(DumperOptions().apply {
                    this.defaultFlowStyle = DumperOptions.FlowStyle.FLOW
                })).dump(
                    schema?.toSubDirectory(cacheLocation)?.inputStream()?.use { yamlStream ->
                        StaticDataSchema.fromSchemaYaml(YamlReader(yamlStream).root, it, errLog)
                    } ?: StaticDataSchema.fromFileStream(it, errLog)
                )
            )
        }
        for (x in errLog) {
            printError("$x: $errLog")
        }
        println("Completed with ${errLog.size} errors")


    }


}