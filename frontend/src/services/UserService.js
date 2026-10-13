import axios from "axios";

const API_URL = "/api/admin/users";

const MEMBER_PROFILE_API_URL = "/api/member/profile";

export function uploadProfileImage(file) {
    const formData = new FormData();
    formData.append("file", file);

    return axios.put(
        `${MEMBER_PROFILE_API_URL}/image`,
        formData
    );
}
export function getMemberProfile() {
    return axios.get(MEMBER_PROFILE_API_URL);
}

export function updateMemberProfile(profile) {
    return axios.put(MEMBER_PROFILE_API_URL, profile);
}
export function deleteMemberAccount(password) {
    return axios.post(
        `${MEMBER_PROFILE_API_URL}/deletion`,
        { password }
    );
}

export function changeMemberPassword(currentPassword, newPassword) {
    return axios.put(
        `${MEMBER_PROFILE_API_URL}/password`,
        { currentPassword, newPassword }
    );
}
export function getUsers() {
    return axios.get(API_URL);
}

export function getUser(id) {
    return axios.get(`${API_URL}/${id}`);
}

export function createUser(user) {
    return axios.post(API_URL, user);
}

export function updateUser(id, user) {
    return axios.put(`${API_URL}/${id}`, user);
}

export function deleteUser(id) {
    return axios.delete(`${API_URL}/${id}`);
}