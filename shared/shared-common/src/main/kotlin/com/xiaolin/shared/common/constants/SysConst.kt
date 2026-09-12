package com.xiaolin.shared.common.constants

enum class AppType {
    BUSINESS,
    ADMIN,
    PORTAL
}

enum class PermissionType {
    MENU, BUTTON, API, ELEMENT;
}

enum class UserKind {
    NORMAL,
    SUPER_ADMIN,
    SYSTEM;

    fun isNormal(): Boolean {
        return this == NORMAL
    }

    fun isSuperAdmin(): Boolean {
        return this == SUPER_ADMIN
    }
    fun isSystem(): Boolean {
        return this == SYSTEM
    }
}

object TenantStatus {
    const val PENDING = 0
    const val NORMAL = 1
    const val FROZEN = 2
    const val CANCELLED = 3
}

object MemberStatus {
    const val PENDING = 0
    const val NORMAL = 1
    const val DISABLED = 2
    const val QUIT = 3
}

object UserStatus {
    const val INACTIVE = 0
    const val NORMAL = 1
    const val FROZEN = 2
    const val CANCELLED = 3
}

object CommonStatus {
    const val ENABLED = 1
    const val DISABLED = 0
}

object ScopeType {
    const val GLOBAL = "GLOBAL"
    const val TENANT_ALL = "TENANT_ALL"
    const val ORG_TREE = "ORG_TREE"
    const val ORG_SELF = "ORG_SELF"
    const val MEMBER_TEAM = "MEMBER_TEAM"
    const val MEMBER_SELF = "MEMBER_SELF"
    const val CUSTOM_SQL = "CUSTOM_SQL"
    const val DENY_ALL = "DENY_ALL"
}

object ScopeEffect {
    const val ALLOW = "ALLOW"
    const val DENY = "DENY"
}

object SubjectType {
    const val GLOBAL_ROLE = "GLOBAL_ROLE"
    const val ROLE = "ROLE"
    const val MEMBER = "MEMBER"
    const val PERMISSION = "PERMISSION"
}

object AuditModule {
    const val ROLE = "ROLE"
    const val PERMISSION = "PERMISSION"
    const val DATASCOPE = "DATASCOPE"
    const val MEMBER = "MEMBER"
    const val TENANT = "TENANT"
}

/** 表名常量，手写 SQL 时引用，避免拼写错误 */
object SysTable {
    const val TENANT_TYPE = "sys.tenant_type"
    const val APP = "sys.app"
    const val TENANT_TYPE_APP = "sys.tenant_type_app"
    const val PERMISSION = "sys.permission"
    const val IDENTITY_TYPE = "sys.identity_type"
    const val TENANT = "sys.tenant"
    const val ORG = "sys.org"
    const val USER_ACCOUNT = "sys.user_account"
    const val MEMBER = "sys.member"
    const val MEMBER_IDENTITY = "sys.member_identity"
    const val MEMBER_ORG = "sys.member_org"
    const val ROLE = "sys.role"
    const val ROLE_PERMISSION = "sys.role_permission"
    const val MEMBER_ROLE = "sys.member_role"
    const val GLOBAL_ROLE = "sys.global_role"
    const val GLOBAL_ROLE_PERMISSION = "sys.global_role_permission"
    const val USER_GLOBAL_ROLE = "sys.user_global_role"
    const val DATA_OBJECT = "sys.data_object"
    const val DATA_SCOPE_POLICY = "sys.data_scope_policy"
    const val DATA_SCOPE_BINDING = "sys.data_scope_binding"
    const val USER_CONTEXT = "sys.user_context"
    const val LOGIN_LOG = "sys.login_log"
    const val AUDIT_LOG = "sys.audit_log"
}

object Gender {
    const val MALE = 1
    const val FEMALE = 2
    const val UNKNOWN = 0
}
