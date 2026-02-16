import axios from 'axios';

const API_URL = '/api/test/users'; // correspond à ton @GetMapping("/users")

export function getUsers() {
    return axios.get(API_URL);
}
