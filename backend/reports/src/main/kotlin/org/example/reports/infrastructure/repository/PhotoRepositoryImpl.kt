package org.example.reports.infrastructure.repository

import org.example.reports.domain.model.Photo
import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.infrastructure.mapper.PhotoMapper
import org.springframework.stereotype.Repository

@Repository
class PhotoRepositoryImpl(
    private val jpaRepository: PhotoJpaRepository,
    private val mapper: PhotoMapper
) : PhotoRepository {

    override fun findById(id: Long): Photo? {
        return jpaRepository.findById(id)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByReportId(reportId: Long): List<Photo> {
        return jpaRepository.findByReportId(reportId)
            .map { mapper.toDomain(it) }
    }

    override fun save(photo: Photo): Photo {
        val entity = mapper.toEntity(photo)
        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun deleteById(id: Long) {
        jpaRepository.deleteById(id)
    }
}