package org.example.reports.domain.repository

import org.example.reports.domain.model.Photo

interface PhotoRepository {

    fun findById(id: Long): Photo?

    fun findByReportId(reportId: Long): List<Photo>

    fun save(photo: Photo): Photo

    fun deleteById(id: Long)
}