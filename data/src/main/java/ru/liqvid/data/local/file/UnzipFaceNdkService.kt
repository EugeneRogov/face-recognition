package ru.liqvid.data.local.file

import io.reactivex.rxjava3.core.Observable
import net.lingala.zip4j.ZipFile
import java.io.File
import java.io.IOException

class UnzipFaceNdkService {
    companion object {
        val TAG: String = UnzipFaceNdkService::class.java.simpleName
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists).
     *
     * Check hash of zip file.
     *
     * Delete zip file
     *
     * @param zipFilePath
     * @param destDirectory
     * @param digest
     * @throws IOException
     */
    @Throws(IOException::class)
    fun unzip(zipFilePath: String, destDirectory: String, digest: String): Observable<Boolean> {
        return Observable.create { subscriber ->
            try {
                val hash = ru.liqvid.util.FileUtils.calculateMD5(File(zipFilePath))
                if (hash.equals(digest)) {
                    ZipFile(zipFilePath).extractAll(destDirectory)
                    subscriber.onNext(true)
                } else
                    subscriber.onNext(false)
                File(zipFilePath).deleteRecursively()
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
    }

}