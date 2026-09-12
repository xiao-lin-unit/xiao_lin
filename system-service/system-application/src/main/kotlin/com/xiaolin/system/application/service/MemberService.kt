package com.xiaolin.system.application.service

import com.xiaolin.shared.infra.components.IdGenerator
import com.xiaolin.system.application.command.MemberCreateCommand
import com.xiaolin.system.domain.aggregation.MemberAggregation
import com.xiaolin.system.domain.repository.MemberRepository
import com.xiaolin.system.domain.repository.TenantRepository
import com.xiaolin.system.domain.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val tenantRepository: TenantRepository,
    private val userRepository: UserRepository,
    private val idGenerator: IdGenerator,
) {

    @Transactional
    fun create(command: MemberCreateCommand): MemberAggregation {
        val tenantId = requireNotNull(command.tenantId) { "成员所属租户不能为空" }
        val userId = requireNotNull(command.userId) { "成员关联用户不能为空" }

        // uk_member_tenant_user：同一租户下同一用户只能有一个成员身份
        require(!memberRepository.existsByTenantAndUser(tenantId, userId)) {
            "该用户已是当前租户成员"
        }

        val tenant = tenantRepository.getById(tenantId)
        require(!tenant.deleted) { "租户不存在或已删除: $tenantId" }
        // 外键 sys.member -> sys.user_account
        val user = userRepository.getById(userId)
        require(!user.isDeleted()) { "用户不存在或已删除: $userId" }

        val member = MemberAggregation.create(idGenerator.nextId())
            .tenantId(tenantId)
            .userId(userId)
            .memberNo(command.memberNo)
            .displayName(command.displayName ?: user.realName)
            .orgId(command.orgId)
            .isTenantAdmin(command.isTenantAdmin)
            .status(command.status)
            .profile(command.profile)
            .invitedBy(command.invitedBy)
            .build()

        member.verify()
        memberRepository.create(member)
        return member
    }

    @Transactional
    fun update(id: Long, command: MemberCreateCommand): MemberAggregation {
        val member = memberRepository.getById(id)
        require(!member.deleted) { "成员已删除: $id" }

        command.orgId?.let { member.orgId = it }
        command.memberNo?.let { member.memberNo = it }
        command.displayName?.let { member.displayName = it }
        command.isTenantAdmin?.let { member.isTenantAdmin = it }
        command.status?.let { member.status = it }
        command.profile?.let { member.profile = it }

        member.verify()
        memberRepository.update(member)
        return member
    }

    @Transactional
    fun delete(id: Long) {
        val member = memberRepository.getById(id)
        require(!member.deleted) { "成员已删除: $id" }
        memberRepository.deleteById(id)
    }

    /** 审核通过 */
    @Transactional
    fun approve(id: Long): MemberAggregation {
        val member = memberRepository.getById(id)
        member.approve()
        memberRepository.update(member)
        return member
    }

    @Transactional
    fun disable(id: Long): MemberAggregation {
        val member = memberRepository.getById(id)
        member.disable()
        memberRepository.update(member)
        return member
    }

    /** 退出：业务动作，记录保留 */
    @Transactional
    fun quit(id: Long): MemberAggregation {
        val member = memberRepository.getById(id)
        member.quit()
        memberRepository.update(member)
        return member
    }

    @Transactional
    fun grantTenantAdmin(id: Long): MemberAggregation {
        val member = memberRepository.getById(id)
        member.grantTenantAdmin()
        memberRepository.update(member)
        return member
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): MemberAggregation = memberRepository.getById(id)

    @Transactional(readOnly = true)
    fun list(tenantId: Long, pageable: Pageable): Page<MemberAggregation> =
        memberRepository.list(tenantId, pageable)

    /** 用户的全部租户身份，用于"切换租户"下拉 */
    @Transactional(readOnly = true)
    fun listByUserId(userId: Long): List<MemberAggregation> = memberRepository.listByUserId(userId)
}
