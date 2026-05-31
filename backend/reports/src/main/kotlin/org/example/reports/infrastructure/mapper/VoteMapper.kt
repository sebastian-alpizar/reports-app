package org.example.reports.infrastructure.mapper

import org.example.reports.domain.model.Vote
import org.example.reports.infrastructure.entity.VoteEntity
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", uses = [VoteMapper::class], unmappedSourcePolicy = ReportingPolicy.IGNORE)
interface VoteMapper {
    fun toDomain(entity: VoteEntity): Vote
    fun toEntity(domain: Vote): VoteEntity
}