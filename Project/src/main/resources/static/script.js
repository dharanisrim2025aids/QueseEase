```javascript
const API = "http://localhost:8081/api";

let doctors = [];
let patients = [];
let tokens = [];

function showToast(message, isError = false) {
    const toast = document.getElementById("toast");

    toast.textContent = message;
    toast.className = "toast show" + (isError ? " error" : "");

    setTimeout(() => {
        toast.className = "toast";
    }, 3000);
}

async function apiRequest(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });

    const text = await response.text();

    if (!response.ok) {
        throw new Error(text || `Request failed: ${response.status}`);
    }

    return text ? JSON.parse(text) : null;
}

function showSection(sectionId, button) {
    document.querySelectorAll(".section").forEach(section => {
        section.classList.add("hidden");
    });

    document.getElementById(sectionId).classList.remove("hidden");

    document.querySelectorAll(".tab").forEach(tab => {
        tab.classList.remove("active");
    });

    button.classList.add("active");
}

function getStatusBadge(status) {
    const value = (status || "").toUpperCase();

    let className = "waiting";

    if (value === "SERVING") className = "serving";
    if (value === "COMPLETED") className = "completed";
    if (value === "CANCELLED") className = "cancelled";

    return `<span class="badge ${className}">${escapeHTML(value)}</span>`;
}

function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    })[character]);
}

function doctorName(id) {
    const doctor = doctors.find(d => Number(d.id) === Number(id));
    return doctor ? doctor.doctorName : `Doctor #${id ?? "-"}`;
}

function patientName(id) {
    const patient = patients.find(p => Number(p.id) === Number(id));
    return patient ? patient.patientName : `Patient #${id ?? "-"}`;
}

async function loadDoctors() {
    doctors = await apiRequest(`${API}/doctor/getall`);

    document.getElementById("doctorCount").textContent = doctors.length;

    document.getElementById("doctorTable").innerHTML =
        doctors.length
            ? doctors.map(doctor => `
<tr>
<td>${doctor.id}</td>
<td>${escapeHTML(doctor.doctorName)}</td>
<td>${escapeHTML(doctor.specialization)}</td>
<td>${doctor.averageConsultationTime} min</td>
<td>${doctor.currentServingToken}</td>
<td>
    <button class="delete-btn"
            onclick="deleteDoctor(${doctor.id})">Delete</button>
</td>
</tr>
`).join("")
            : `<tr><td colspan="6" class="empty">No doctors found</td></tr>`;

    const options = doctors.map(doctor =>
        `<option value="${doctor.id}">${escapeHTML(doctor.doctorName)} - ${escapeHTML(doctor.specialization)}</option>`
    ).join("");

    document.getElementById("tokenDoctor").innerHTML =
        `<option value="">Select doctor</option>${options}`;

document.getElementById("advanceDoctor").innerHTML =
    `<option value="">Select doctor</option>${options}`;
}

async function loadPatients() {
    patients = await apiRequest(`${API}/patient/getall`);

    document.getElementById("patientCount").textContent = patients.length;

    document.getElementById("patientTable").innerHTML =
        patients.length
            ? patients.map(patient => `
                <tr>
                    <td>${patient.id}</td>
                    <td>${escapeHTML(patient.patientName)}</td>
                    <td>${patient.age}</td>
                    <td>${escapeHTML(patient.gender)}</td>
                    <td>${escapeHTML(patient.contactNumber)}</td>
                    <td>
                        <button class="delete-btn"
                            onclick="deletePatient(${patient.id})">Delete</button>
                    </td>
                </tr>
            `).join("")
            : `<tr><td colspan="6" class="empty">No patients found</td></tr>`;

    document.getElementById("tokenPatient").innerHTML =
        `<option value="">Select patient</option>` +
        patients.map(patient =>
            `<option value="${patient.id}">${escapeHTML(patient.patientName)} (#${patient.id})</option>`
        ).join("");
}

async function loadTokens() {
    tokens = await apiRequest(`${API}/token/getall`);

    document.getElementById("tokenCount").textContent = tokens.length;

    document.getElementById("waitingCount").textContent =
        tokens.filter(token => token.status === "WAITING").length;

    const rows = tokens.map(token => `
        <tr>
            <td>${token.id}</td>
            <td>#${token.tokenNumber}</td>
            <td>${escapeHTML(patientName(token.patient?.id))}</td>
            <td>${escapeHTML(doctorName(token.doctor?.id))}</td>
            <td>${escapeHTML(token.tokenDate)}</td>
            <td>
                <span class="badge ${token.isPriority ? "priority" : "normal"}">
                    ${token.isPriority ? "Priority" : "Normal"}
                </span>
            </td>
            <td>${getStatusBadge(token.status)}</td>
            <td>${token.estimatedWaitTime} min</td>
            <td>
                <button class="delete-btn"
                    onclick="deleteToken(${token.id})">Delete</button>
            </td>
        </tr>
    `).join("");

    document.getElementById("tokenTable").innerHTML =
        rows || `<tr><td colspan="9" class="empty">No tokens found</td></tr>`;

    const recent = [...tokens].reverse().slice(0, 8);

    document.getElementById("dashboardTokens").innerHTML =
        recent.length
            ? recent.map(token => `
                <tr>
                    <td>#${token.tokenNumber}</td>
                    <td>${escapeHTML(patientName(token.patient?.id))}</td>
                    <td>${escapeHTML(doctorName(token.doctor?.id))}</td>
                    <td>${escapeHTML(token.tokenDate)}</td>
                    <td>${getStatusBadge(token.status)}</td>
                    <td>${token.estimatedWaitTime} min</td>
                </tr>
            `).join("")
            : `<tr><td colspan="6" class="empty">No tokens available</td></tr>`;
}

async function loadDashboard() {
    try {
        await Promise.all([
            loadDoctors(),
            loadPatients()
        ]);

        await loadTokens();
    } catch (error) {
        console.error(error);
        showToast("Unable to load data. Check backend connection.", true);
    }
}

// CREATE PATIENT
document.getElementById("patientForm").addEventListener("submit", async event => {
    event.preventDefault();

    const data = {
        patientName: document.getElementById("patientName").value.trim(),
        age: Number(document.getElementById("patientAge").value),
        gender: document.getElementById("patientGender").value,
        contactNumber: document.getElementById("patientContact").value.trim()
    };

    try {
        await apiRequest(`${API}/patient/create`, {
            method: "POST",
            body: JSON.stringify(data)
        });

        event.target.reset();
        showToast("Patient added successfully!");
        await loadPatients();
    } catch (error) {
        console.error(error);
        showToast("Unable to add patient.", true);
    }
});

// CREATE DOCTOR
document.getElementById("doctorForm").addEventListener("submit", async event => {
    event.preventDefault();

    const data = {
        doctorName: document.getElementById("doctorName").value.trim(),
        specialization: document.getElementById("specialization").value.trim(),
        averageConsultationTime: Number(document.getElementById("consultationTime").value),
        currentServingToken: Number(document.getElementById("currentToken").value)
    };

    try {
        await apiRequest(`${API}/doctor/create`, {
            method: "POST",
            body: JSON.stringify(data)
        });

        event.target.reset();
        showToast("Doctor added successfully!");
        await loadDoctors();
    } catch (error) {
        console.error(error);
        showToast("Unable to add doctor.", true);
    }
});

// GENERATE TOKEN
document.getElementById("tokenForm").addEventListener("submit", async event => {
    event.preventDefault();

    const doctorId = document.getElementById("tokenDoctor").value;
    const patientId = document.getElementById("tokenPatient").value;
    const type = document.getElementById("tokenType").value;

    if (!doctorId || !patientId) {
        showToast("Please select doctor and patient.", true);
        return;
    }

    const endpoint = type === "priority" ? "priority" : "generate";

    try {
        await apiRequest(
            `${API}/token/${endpoint}?doctorId=${doctorId}&patientId=${patientId}`,
            { method: "POST" }
        );

        showToast("Token generated successfully!");
        await loadTokens();
    } catch (error) {
        console.error(error);
        showToast(error.message || "Unable to generate token.", true);
    }
});

// ADVANCE QUEUE
async function advanceQueue() {
    const doctorId = document.getElementById("advanceDoctor").value;

    if (!doctorId) {
        showToast("Please select a doctor.", true);
        return;
    }

    try {
        await apiRequest(`${API}/token/advance/${doctorId}`, {
            method: "PUT"
        });

        showToast("Next token is now serving!");
        await loadTokens();
        await loadDoctors();
    } catch (error) {
        console.error(error);
        showToast(error.message || "Unable to advance queue.", true);
    }
}

// DELETE PATIENT
async function deletePatient(id) {
    if (!confirm("Are you sure you want to delete this patient?")) return;

    try {
        await apiRequest(`${API}/patient/delete/${id}`, {
            method: "DELETE"
        });

        showToast("Patient deleted successfully!");
        await loadPatients();
    } catch (error) {
        console.error(error);
        showToast("Unable to delete patient. Check if tokens are linked.", true);
    }
}

// DELETE DOCTOR
async function deleteDoctor(id) {
    if (!confirm("Are you sure you want to delete this doctor?")) return;

    try {
        await apiRequest(`${API}/doctor/delete/${id}`, {
            method: "DELETE"
        });

        showToast("Doctor deleted successfully!");
        await loadDoctors();
    } catch (error) {
        console.error(error);
        showToast("Unable to delete doctor. Check if tokens are linked.", true);
    }
}

// DELETE TOKEN
async function deleteToken(id) {
    if (!confirm("Are you sure you want to delete this token?")) return;

    try {
        await apiRequest(`${API}/token/delete/${id}`, {
            method: "DELETE"
        });

        showToast("Token deleted successfully!");
        await loadTokens();
    } catch (error) {
        console.error(error);
        showToast("Unable to delete token.", true);
    }
}

// INITIAL LOAD
document.addEventListener("DOMContentLoaded", loadDashboard);
```
