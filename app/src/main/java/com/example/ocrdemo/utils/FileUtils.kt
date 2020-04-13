package com.example.ocrdemo.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import java.io.*
import java.nio.charset.Charset
import java.util.logging.Level
import java.util.logging.Logger


/**
 *
 * Author: <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * Time: Created 2020/4/13 10:43 AM
 * Project: OCRDemo
 * Description:
 **/
class FileUtils {

    companion object{

        private const val TAG = "FileUtils"

        /**
         * 获取文件扩展名
         *
         * @param file
         * @return
         */
        fun getFileLastName(file: File): String? {
            val fileName: String = file.name
            return fileName.substring(fileName.lastIndexOf(".") + 1)
        }


        /**
         * 打开指目录
         *
         * @param context
         * @param dirPath
         */
        fun openAssignFolder(context: Context, dirPath: String?) {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setDataAndType(Uri.parse(dirPath), "*/*")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
//        startActivity(Intent.createChooser(intent, "选择浏览工具"));
        }


        /**
         * 修改文件内容
         *
         * @param filePath 文件地址 d:/1.txt
         * @param oldstr   旧字符串
         * @param newStr   新字符串
         * @return
         */
        fun modifyFileContent(filePath: String, oldstr: String, newStr: String): Boolean {
            val file = File(filePath)
            var fileReader: FileReader? = null
            var fileWriter: FileWriter? = null
            val stringBuilder = StringBuilder()
            var flag = 0
            val temp = CharArray(1024)
            try {
                fileReader = FileReader(file)
                while (fileReader.read(temp).also { flag = it } != -1) {
                    stringBuilder.append(temp)
                }
                val content = stringBuilder.toString().replace(oldstr, newStr)
                fileWriter = FileWriter(file)
                fileWriter.write(content)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fileReader?.close()
                    fileWriter?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return true
        }

        /**
         * 复制单个文件
         *
         * @param oldPath String 原文件路径 如：c:/fqf.txt
         * @param newPath String 复制后路径 如：f:/fqf.txt
         * @return boolean
         */
        fun copyFile(oldPath: String, newPath: String) {
            try {
                var bytesum = 0
                var byteread = 0
                val oldfile = File(oldPath)
                if (oldfile.exists()) { //文件存在时
                    val inStream: InputStream = FileInputStream(oldPath) //读入原文件
                    val fs = FileOutputStream(newPath)
                    val buffer = ByteArray(1444)
                    var length: Int
                    while (inStream.read(buffer).also { byteread = it } != -1) {
                        bytesum += byteread //字节数 文件大小
                        println(bytesum)
                        fs.write(buffer, 0, byteread)
                    }
                    inStream.close()
                }
            } catch (e: Exception) {
                println("复制单个文件操作出错")
                e.printStackTrace()
            }
        }

        /**
         * 创建文件
         *
         * @param filePath 文件地址
         * @param fileName 文件名
         * @return
         */
        fun createFile(filePath: String, fileName: String): Boolean {
            val strFilePath = filePath + fileName
            val file = File(filePath)
            if (!file.exists()) {
                /**  注意这里是 mkdirs()方法  可以创建多个文件夹  */
                file.mkdirs()
            }
            val subfile = File(strFilePath)
            if (!subfile.exists()) {
                try {
                    return subfile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                return true
            }
            return false
        }

        /**
         * 遍历文件夹下的文件
         *
         * @param file 地址
         */
        fun getFile(file: File): List<File>? {
            val list: MutableList<File> = mutableListOf()
            val fileArray: Array<File> = file.listFiles() ?: arrayOf()
            for (f in fileArray) {
                if (f.isFile) {
                    list.add(0, f)
                } else {
                    getFile(f)
                }
            }
            return list
        }

        fun getDirectoryLength(directory: File): Long {
            if (directory.isFile) return directory.length()
            val children: Array<File> = directory.listFiles() ?: arrayOf()
            var total: Long = 0
            for (child in children) total += getDirectoryLength(child)
            return total
        }

        fun deleteDirectory(file: File): Boolean {
            if (!file.exists()) {
                return false
            }
            if (file.isDirectory) {
                val files: Array<File> = file.listFiles() ?: arrayOf()
                for (f in files) {
                    deleteDirectory(f)
                }
            }
            return file.delete()
        }

        /**
         * 删除文件
         *
         * @param filePath 文件地址
         * @return
         */
        fun deleteFiles(filePath: String): Boolean {
            val files: List<File>? = getFile(File(filePath))
            if (files!!.isNotEmpty()) {
                for (i in files.indices) {
                    val file: File = files[i]
                    /**  如果是文件则删除  如果都删除可不必判断   */
                    if (file.isFile()) {
                        file.delete()
                    }
                }
            }
            return true
        }

        fun writeToNewFile(path: String, content: String): Boolean? {
            return try {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                } else {
                    val parentFile: File = file.getParentFile()
                    if (!parentFile.exists()) parentFile.mkdirs()
                }
                val fileWriter = FileWriter(path, false)
                val writer = BufferedWriter(fileWriter)
                writer.append(content)
                writer.flush()
                writer.close()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        /**
         * 向文件中添加内容
         *
         * @param strcontent 内容
         * @param filePath   地址
         * @param fileName   文件名
         */
        fun writeToFile(strcontent: String, filePath: String, fileName: String) {
            //生成文件夹之后，再生成文件，不然会出错
            val strFilePath = filePath + fileName
            // 每次写入时，都换行写
            val subfile = File(strFilePath)
            var raf: RandomAccessFile? = null
            try {
                /**   构造函数 第二个是读写方式     */
                raf = RandomAccessFile(subfile, "rw")
                /**  将记录指针移动到该文件的最后   */
                raf.seek(subfile.length())
                /** 向文件末尾追加内容   */
                raf.write(strcontent.toByteArray())
                raf.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 修改文件内容（覆盖或者添加）
         *
         * @param path    文件地址
         * @param content 覆盖内容
         * @param append  指定了写入的方式，是覆盖写还是追加写(true=追加)(false=覆盖)
         */
        fun modifyFile(path: String, content: String, append: Boolean) {
            try {
                val fileWriter = FileWriter(path, append)
                val writer = BufferedWriter(fileWriter)
                writer.append(content)
                writer.flush()
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        @Throws(IOException::class)
        fun readTextFromUri(context: Context, uri: Uri): String? {
            var context1: Context = context
            context1 = context1.applicationContext
            val stringBuilder = StringBuilder()
            val inputStream1: InputStream? = context1.contentResolver.openInputStream(uri)
            if (inputStream1 != null) {
                val bufferedReader = BufferedReader(InputStreamReader(inputStream1))
                var line: String? = bufferedReader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = bufferedReader.readLine()
                }
            }
            return stringBuilder.toString()
        }

        fun getContent(file: File): String? {
            val inputStream: FileInputStream?
            try {
                inputStream = FileInputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return null
            }
            val inputStreamReader: InputStreamReader?
            inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            val sb = StringBuffer()
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                    sb.append("\n")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return sb.toString()
        }

        /**
         * 读取文件内容
         *
         * @param filePath 地址
         * @param filename 名称
         * @return 返回内容
         */
        fun getString(filePath: String, filename: String): String? {
            return getContent(File(filePath + filename))
        }

        /**
         * 重命名文件
         *
         * @param oldPath 原来的文件地址
         * @param newPath 新的文件地址
         */
        fun renameFile(oldPath: String, newPath: String) {
            val oleFile = File(oldPath)
            val newFile = File(newPath)
            //执行重命名
            oleFile.renameTo(newFile)
        }

        /**
         * 复制文件目录
         *
         * @param fromFile 要复制的文件目录
         * @param toFile   要粘贴的文件目录
         * @return 是否复制成功
         */
        fun copyDirectory(fromFile: String, toFile: String): Boolean {
            //要复制的文件目录
            val currentFiles: Array<File>
            val root = File(fromFile)
            //如同判断SD卡是否存在或者文件是否存在
            //如果不存在则 return出去
            if (!root.exists()) {
                return false
            }
            //如果存在则获取当前目录下的全部文件 填充数组
            currentFiles = root.listFiles()

            //目标目录
            val targetDir = File(toFile)
            //创建目录
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            //遍历要复制该目录下的全部文件
            for (i in currentFiles.indices) {
                if (currentFiles[i].isDirectory) //如果当前项为子目录 进行递归
                {
                    copyDirectory(
                        currentFiles[i].getPath().toString() + "/",
                        toFile + currentFiles[i].getName().toString() + "/"
                    )
                } else  //如果当前项为文件则进行文件拷贝
                {
                    CopySdcardFile(
                        currentFiles[i].getPath(),
                        toFile + currentFiles[i].getName()
                    )
                }
            }
            return true
        }


        //文件拷贝
        //要复制的目录下的所有非子目录(文件夹)文件拷贝
        fun CopySdcardFile(fromFile: String, toFile: String): Boolean {
            return try {
                val fosfrom: InputStream = FileInputStream(fromFile)
                val fosto: OutputStream = FileOutputStream(toFile)
                val bt = ByteArray(1024)
                var c: Int
                while (fosfrom.read(bt).also { c = it } > 0) {
                    fosto.write(bt, 0, c)
                }
                fosfrom.close()
                fosto.close()
                true
            } catch (ex: Exception) {
                false
            }
        }


        fun getFilePath(context: Context, uri: Uri): String? {
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection = arrayOf("_data")
                val cursor: Cursor?
                try {
                    cursor = context.contentResolver.query(uri, projection, null, null, null)
                    cursor?.let {
                        val column_index: Int = cursor.getColumnIndexOrThrow("_data")
                        if (cursor.moveToFirst()) {
                            return cursor.getString(column_index)
                        }
                        cursor.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        return uri.toString()
                            .replace("content://com.android.fileexplorer.myprovider/root_files", "")
                    } catch (e1: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
                return uri.getPath()
            }
            return null
        }

        fun fileToBytes(filePath: String): ByteArray? {
            var buffer: ByteArray? = null
            val file = File(filePath)
            var fis: FileInputStream? = null
            var bos: ByteArrayOutputStream? = null
            try {
                fis = FileInputStream(file)
                bos = ByteArrayOutputStream()
                val b = ByteArray(1024)
                var n: Int
                while (fis.read(b).also { n = it } != -1) {
                    bos.write(b, 0, n)
                }
                buffer = bos.toByteArray()
            } catch (ex: FileNotFoundException) {
                Logger.getLogger(TAG).log(Level.SEVERE, null, ex)
            } catch (ex: IOException) {
                Logger.getLogger(TAG).log(Level.SEVERE, null, ex)
            } finally {
                try {
                    bos?.close()
                } catch (ex: IOException) {
                    Logger.getLogger(TAG).log(Level.SEVERE, null, ex)
                } finally {
                    try {
                        fis?.close()
                    } catch (ex: IOException) {
                        Logger.getLogger(TAG).log(Level.SEVERE, null, ex)
                    }
                }
            }
            return buffer
        }

        fun bytesToFile(buffer: ByteArray, filePath: String) {
            val file = File(filePath)
            var output: OutputStream? = null
            var bufferedOutput: BufferedOutputStream? = null
            try {
                output = FileOutputStream(file)
                bufferedOutput = BufferedOutputStream(output)
                bufferedOutput.write(buffer)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (null != bufferedOutput) {
                    try {
                        bufferedOutput.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                if (null != output) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }




    }
}